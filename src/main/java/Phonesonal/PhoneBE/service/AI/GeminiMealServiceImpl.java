package Phonesonal.PhoneBE.service.AI;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.domain.*;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.FoodRepository;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.web.dto.Food.GenerateMealRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiMealServiceImpl implements GeminiMealService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final FoodRepository foodRepository;
    private final RecommendMealRepository recommendMealRepository;

    /* ===================== Public APIs ===================== */

    @Override
    public String generateWeeklyAllMeals(User user,
                                         Diagnosis diagnosis,
                                         List<Food> availableFoods) {
        // 1) 한 번에 7일 생성 시도
        String basePrompt = buildAllMealsPrompt(user, diagnosis, availableFoods);
        GeminiResult first = callGeminiWithRetry(basePrompt, 3);

        // 2) 정상적으로 파싱되면 바로 반환
        try {
            String cleaned = validateAndClean(first.text);
            return cleaned;
        } catch (Exception e) {
            log.warn("Single-shot JSON parse failed. finishReason={}, fallback to chunked generation.",
                    first.finishReason);
        }

        // 3) MAX_TOKENS 또는 파싱 실패 시: 3개 청크로 분할 생성 (1-3, 4-5, 6-7)
        Map<String, Object> merged = new LinkedHashMap<>();
        mergeDaysInto(merged, callChunk(user, diagnosis, availableFoods, 1, 3));
        mergeDaysInto(merged, callChunk(user, diagnosis, availableFoods, 4, 5));
        mergeDaysInto(merged, callChunk(user, diagnosis, availableFoods, 6, 7));

        // 4) 최종 병합 JSON 직렬화 및 검증
        try {
            String finalJson = objectMapper.writeValueAsString(merged);
            objectMapper.readValue(finalJson, Map.class);
            return finalJson;
        } catch (Exception e) {
            log.error("Chunked merge validation failed", e);
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }
    }

    @Transactional
    @Override
    public int generateAndSaveWeeklyAllMeals(User user,
                                             Diagnosis diagnosis,
                                             GenerateMealRequestDTO req) {
        if (diagnosis == null) {
            throw new CommonExceptionHandler(ErrorStatus.DIAGNOSIS_NOT_FOUND);
        }

        // 1) 후보 음식: is_custom=false
        List<Food> available = foodRepository.findByIsCustomFalse();
        if (available.isEmpty()) throw new IllegalArgumentException("is_custom=false 음식 없음.");

        // 1-1) id -> Food 맵(한 번만 생성)
        Map<Long, Food> foodMap = available.stream()
                .collect(Collectors.toMap(Food::getFoodId, f -> f, (a, b) -> a));

        // 2) Gemini 호출(7일 최종 JSON 문자열 확보; 내부에서 단일/분할 자동 처리)
        String json = generateWeeklyAllMeals(user, diagnosis, available);

        // 3) 파싱
        Map<String, Object> root = readAs(json, new TypeReference<Map<String, Object>>() {});
        if (log.isDebugEnabled()) {
            String peek = json.substring(0, Math.min(400, json.length()));
            log.debug("Gemini meal JSON peek: {}", peek);
        }

        // 3-1) 키 노멀라이즈 + days 래핑 지원
        Map<String, Object> top = normalizeKeysShallow(root);
        if (top.containsKey("days") && top.get("days") instanceof Map<?, ?> days) {
            top = normalizeKeysShallow((Map<?, ?>) days);
        }

        // 구조 검증 (느슨 매칭 + normalize)
        for (int d = 1; d <= 7; d++) {
            String dayKey = "day" + d;

            Map<String, Object> dayObj = safeGetMap(top.get(dayKey));
            if (dayObj == null) {
                dayObj = findLooseMap(top, dayKey);
            }
            if (dayObj == null) {
                throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
            }

            Map<String, Object> dayNorm = normalizeKeysShallow(dayObj);

            for (MealTime mt : EnumSet.of(MealTime.BREAKFAST, MealTime.LUNCH, MealTime.DINNER, MealTime.SNACK)) {
                String mtKey = mt.name().toLowerCase(Locale.ROOT);

                Object raw = dayNorm.get(mtKey);
                if (raw == null && dayNorm.get("meals") instanceof Map<?, ?> meals) {
                    raw = normalizeKeysShallow((Map<?, ?>) meals).get(mtKey);
                }

                List<Map<String, Object>> items = coerceItems(raw);
                if (items == null || items.size() != 3) {
                    throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
                }
            }
        }

        // 4) 시작일 월요일 보정
        LocalDate start = req.getStartDate();
        if (start.getDayOfWeek() != DayOfWeek.MONDAY) {
            start = start.with(DayOfWeek.MONDAY);
        }

        // 5) GoalPeriod 결정
        GoalPeriod gp = resolveGoalPeriod(user);

        // 6) 저장(grams만 저장)
        int saved = 0;
        for (int d = 1; d <= 7; d++) {
            String dayKey = "day" + d;
            Map<String, Object> dayObj = safeGetMap(top.get(dayKey));
            if (dayObj == null) {
                dayObj = findLooseMap(top, dayKey);
            }
            if (dayObj == null) {
                log.warn("응답에 {} 없음. available top keys={}", dayKey, top.keySet());
                continue;
            }
            Map<String, Object> dayNorm = normalizeKeysShallow(dayObj);
            LocalDate date = start.plusDays(d - 1);

            for (MealTime mt : EnumSet.of(MealTime.BREAKFAST, MealTime.LUNCH, MealTime.DINNER, MealTime.SNACK)) {
                String mtKey = mt.name().toLowerCase(Locale.ROOT);

                Object raw = dayNorm.get(mtKey);
                if (raw == null && dayNorm.get("meals") instanceof Map<?, ?> meals) {
                    raw = normalizeKeysShallow((Map<?, ?>) meals).get(mtKey);
                }

                List<Map<String, Object>> items = coerceItems(raw);
                if (items == null || items.isEmpty()) {
                    log.warn("{}-{} 항목 없음. dayKeys={}", dayKey, mt, dayNorm.keySet());
                    continue;
                }

                Set<Long> seen = new HashSet<>();
                List<Map<String, Object>> uniq = new ArrayList<>(3);
                for (Map<String, Object> m : items) {
                    Long fid = toLong(m.get("foodId"));
                    if (fid == null) fid = toLong(m.get("id"));
                    if (fid != null && seen.add(fid)) {
                        uniq.add(m);
                        if (uniq.size() == 3) break;
                    }
                }
                if (uniq.size() != 3) {
                    log.warn("{}-{} 중복 제거 후 3개 미만. size={}", dayKey, mt, uniq.size());
                    continue;
                }

                for (Map<String, Object> item : uniq) {
                    Long foodId = toLong(item.get("foodId"));
                    if (foodId == null) foodId = toLong(item.get("id"));

                    Integer grams = toInt(item.get("grams"));
                    if (grams == null) grams = toInt(item.get("quantity"));

                    Food food = foodMap.get(foodId);
                    if (food == null) {
                        throw new IllegalArgumentException("유효하지 않은 foodId: " + foodId);
                    }

                    RecommendMeal rm = RecommendMeal.builder()
                            .date(date)
                            .mealTime(mt)
                            .quantity(grams == null ? null : grams.floatValue())
                            .food(food)
                            .goalPeriod(gp)
                            .user(user)
                            .build();

                    recommendMealRepository.save(rm);
                    saved++;
                }
            }
        }
        return saved;
    }

    /* ===================== Prompt ===================== */

    private String buildAllMealsPrompt(User user,
                                       Diagnosis diagnosis,
                                       List<Food> availableFoods) {
        StringBuilder userInfo = new StringBuilder();
        userInfo.append(String.format("- 체중: %.1f kg%n", as1(user.getWeight())));
        userInfo.append(String.format("- 키: %.1f cm%n", as1(user.getHeight())));
        userInfo.append(String.format("- 나이: %d세%n", user.getAge()));
        if (user.getGender() != null) userInfo.append(String.format("- 성별: %s%n", user.getGender().name()));
        if (user.getPurpose() != null) userInfo.append(String.format("- 목표: %s%n", user.getPurpose().name()));
        if (user.getBodyFatRate() != null) userInfo.append(String.format("- 체지방률: %.1f%%%n", as1(user.getBodyFatRate())));
        if (user.getMuscleMass() != null) userInfo.append(String.format("- 골격근량: %.1f kg%n", as1(user.getMuscleMass())));

        StringBuilder diag = new StringBuilder();
        if (diagnosis != null) {
            diag.append(String.format("- 권장 칼로리: %d kcal/day%n", diagnosis.getRecommendedCalories()));
            diag.append(String.format("- 권장 영양소 성향: %s%n", diagnosis.getRecommendedNutrition()));
            diag.append(String.format("- 주간 운동 빈도: %d일%n", diagnosis.getWorkoutFrequency()));
        }

        String foodsJson = availableFoods.stream()
                .map(f -> String.format("{\"foodId\": %d, \"name\": \"%s\"}", f.getFoodId(), escape(f.getName())))
                .collect(Collectors.joining(", "));

        String kcalGuide = """
    아침 25%%, 점심 35%%, 저녁 30%%, 간식 10%% 분배를 권장하되, +-10%% 내 조정 가능.
    """;

        return String.format("""
SYSTEM: You are a JSON API. Return ONLY valid JSON. No prose, no explanations, no markdown. Do NOT wrap in code fences.

사용자 체형과 운동 목적을 고려하여 7일간의 식단을 생성한다.
각 날짜(day1~day7)는 BREAKFAST/LUNCH/DINNER/SNACK 네 끼를 모두 포함하고,
각 끼니마다 '서로 다른 음식 3개'를 선택하라.
모든 값은 숫자만. 단위 텍스트 금지.

[사용자 정보]
%1$s

[진단 정보]
%2$s

[사용 가능한 음식 목록(is_custom=false, id만 사용)]
[%3$s]

[영양 가이드]
%4$s

[규칙]
1) JSON 최상위는 day1..day7 키, 정확히 7일치
2) 각 day는 { "BREAKFAST": [...], "LUNCH": [...], "DINNER": [...], "SNACK": [...] }
3) 각 리스트는 정확히 3개 아이템, 배열만 사용 (빈 값 금지)
4) 아이템 스키마:
   { "foodId": number, "grams": number, "calorie": number, "carb": number, "protein": number, "fat": number }
5) grams는 g 기준, calorie/carb/protein/fat는 grams 기준의 값
6) 하루 총칼로리가 권장 칼로리와 ±10%% 이내
7) **모든 끼니 키는 반드시 대문자: BREAKFAST, LUNCH, DINNER, SNACK**
8) 반드시 순수 JSON만 반환

[응답 예시]
{
  "day1": {
    "BREAKFAST": [
      { "foodId": 1, "grams": 100, "calorie": 120, "carb": 20, "protein": 10, "fat": 5 },
      { "foodId": 2, "grams": 80, "calorie": 90, "carb": 15, "protein": 5, "fat": 3 },
      { "foodId": 3, "grams": 50, "calorie": 60, "carb": 10, "protein": 4, "fat": 2 }
    ],
    "LUNCH": [...],
    "DINNER": [...],
    "SNACK": [...]
  },
  "day2": { ... },
  ...
  "day7": { ... }
}
""", userInfo, diag, foodsJson, kcalGuide);
    }

    // 청크 전용 프롬프트 (예: day1~day3만 생성)
    private String buildMealsPromptForDayRange(User user,
                                               Diagnosis diagnosis,
                                               List<Food> availableFoods,
                                               int startDay, int endDay) {
        String base = buildAllMealsPrompt(user, diagnosis, availableFoods);
        // 규칙을 더 좁혀: 지정한 키만 포함
        String extra = "\n\n[추가 규칙]\n" +
                "A) 최상위 키는 day" + startDay + "..day" + endDay + "만 포함한다.\n" +
                "B) day1..day7의 다른 키는 포함하지 않는다.\n";
        return base + extra;
    }

    /* ===================== Gemini Call ===================== */

    private static class GeminiResult {
        final String text;
        final String finishReason; // STOP / MAX_TOKENS / SAFETY ...
        GeminiResult(String text, String finishReason) {
            this.text = text;
            this.finishReason = finishReason;
        }
    }

    private GeminiResult callGeminiWithRetry(String prompt, int maxRetries) {
        Exception last = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                GeminiResult res = callGeminiAPI(prompt);
                return res;
            } catch (Exception e) {
                last = e;
                if (i == maxRetries - 1) {
                    log.error("Gemini API 호출 최종 실패", e);
                    throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
                }
                try { Thread.sleep(1000L * (i + 1)); }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
                }
            }
        }
        throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
    }

    @SuppressWarnings("unchecked")
    private GeminiResult callGeminiAPI(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + apiKey;

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("parts", List.of(Map.of("text", prompt)));
        body.put("contents", List.of(userMsg));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2);              // 더 안정적 출력
        generationConfig.put("maxOutputTokens", 2300);         // 토큰 길이 관리
        generationConfig.put("response_mime_type", "application/json");
        body.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> res = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
        Map<String, Object> resBody = res.getBody();
        if (resBody == null) throw new CommonExceptionHandler(ErrorStatus.AI_API_EMPTY_RESPONSE);

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) resBody.get("candidates");
        if (candidates == null || candidates.isEmpty()) throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_CANDIDATES);

        Map<String, Object> first = candidates.get(0);
        Object finish = first.get("finishReason");
        String finishReason = (finish == null) ? null : String.valueOf(finish);
        if (finishReason != null && !"STOP".equals(finishReason)) {
            log.warn("Gemini finishReason: {}", finishReason);
        }

        Map<String, Object> content = (Map<String, Object>) first.get("content");
        if (content == null) throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_CONTENT);

        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_PARTS);

        Object text = parts.get(0).get("text");
        if (text == null) {
            log.warn("Gemini response has no text part. First part keys: {}", parts.get(0).keySet());
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }

        String raw = String.valueOf(text);
        if (log.isDebugEnabled()) {
            // 요청이 길어질 수 있어 전체를 찍어 달라는 니즈가 있으면 아래 유지
            log.warn("Gemini FULL raw response:\n{}", raw);
        }
        return new GeminiResult(raw, finishReason);
    }

    // day 범위 호출 후 JSON(Map) 반환
    private Map<String, Object> callChunk(User user,
                                          Diagnosis diagnosis,
                                          List<Food> foods,
                                          int startDay, int endDay) {
        String prompt = buildMealsPromptForDayRange(user, diagnosis, foods, startDay, endDay);
        GeminiResult res = callGeminiWithRetry(prompt, 3);

        // 부분 응답도 strip → parse 시도
        String cleaned = validateAndClean(res.text);
        Map<String, Object> chunkMap = readAs(cleaned, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> norm = normalizeKeysShallow(chunkMap);

        // 지정된 day만 골라 반환
        Map<String, Object> onlyRange = new LinkedHashMap<>();
        for (int d = startDay; d <= endDay; d++) {
            String key = "day" + d;
            Map<String, Object> v = safeGetMap(norm.get(key));
            if (v == null) v = findLooseMap(norm, key);
            if (v == null) throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
            onlyRange.put(key, v);
        }
        return onlyRange;
    }

    // 병합 유틸
    private void mergeDaysInto(Map<String, Object> dest, Map<String, Object> part) {
        for (Map.Entry<String, Object> e : part.entrySet()) {
            dest.put(e.getKey(), e.getValue());
        }
    }

    /* ===================== Validation & Cleaning ===================== */

    private String validateAndClean(String response) {
        if (response == null || response.trim().isEmpty())
            throw new CommonExceptionHandler(ErrorStatus.AI_API_EMPTY_RESPONSE);

        log.warn("Gemini FULL raw response:\n{}", response);

        String clean = stripToJson(response);
        try {
            objectMapper.readValue(clean, Map.class);
            return clean;
        } catch (Exception ignored) {
            try {
                List<?> arr = objectMapper.readValue(clean, List.class);
                Map<String, Object> wrapped = new HashMap<>();
                wrapped.put("days", arr);
                return objectMapper.writeValueAsString(wrapped);
            } catch (Exception e2) {
                throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
            }
        }
    }

    private String stripToJson(String s) {
        String t = s.replaceAll("```[a-zA-Z]*", "").replaceAll("```", "").trim();

        int objStart = t.indexOf('{');
        int arrStart = t.indexOf('[');

        if (objStart == -1 && arrStart == -1) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }

        boolean startsWithObject = (objStart != -1) && (arrStart == -1 || objStart < arrStart);
        char open = startsWithObject ? '{' : '[';
        char close = startsWithObject ? '}' : ']';

        int start = startsWithObject ? objStart : arrStart;
        int end = t.lastIndexOf(close);
        if (end == -1 || start >= end) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }

        String core = t.substring(start, end + 1).trim();
        if (!(core.startsWith("{") || core.startsWith("["))) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }
        return core;
    }

    /* ===================== Helpers ===================== */

    private GoalPeriod resolveGoalPeriod(User user) {
        GoalPeriod gp = user.getGoalPeriod();
        if (gp == null) throw new IllegalStateException("사용자의 GoalPeriod를 찾을 수 없습니다.");
        return gp;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeGetMap(Object o) {
        if (o instanceof Map<?, ?> m) return (Map<String, Object>) m;
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeGetListOfMap(Object o) {
        if (o instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object e : list) if (e instanceof Map<?, ?> m) out.add((Map<String, Object>) m);
            return out;
        }
        return null;
    }

    private Map<String, Object> normalizeKeysShallow(Map<?, ?> in) {
        Map<String, Object> out = new HashMap<>();
        for (Map.Entry<?, ?> e : in.entrySet()) {
            String k = String.valueOf(e.getKey()).trim().toLowerCase(Locale.ROOT);
            out.put(k, e.getValue());
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> findLooseMap(Map<String, Object> root, String key) {
        String target = key.trim().replace(" ", "").toLowerCase(Locale.ROOT);
        for (Map.Entry<String, Object> e : root.entrySet()) {
            String k = e.getKey().trim().replace(" ", "").toLowerCase(Locale.ROOT);
            if (k.equals(target) && e.getValue() instanceof Map<?, ?> m) {
                return (Map<String, Object>) m;
            }
        }
        return null;
    }

    private List<Map<String, Object>> coerceItems(Object raw) {
        List<Map<String, Object>> list = safeGetListOfMap(raw);
        if (list != null) return list;

        if (raw instanceof Map<?, ?> m) {
            Map<String, Object> nm = normalizeKeysShallow(m);
            for (String k : List.of("items", "list", "foods")) {
                List<Map<String, Object>> alt = safeGetListOfMap(nm.get(k));
                if (alt != null) return alt;
            }
        }
        return null;
    }

    private <T> T readAs(String json, TypeReference<T> type) {
        try { return objectMapper.readValue(json, type); }
        catch (Exception e) { throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED); }
    }

    private float as1(BigDecimal v) {
        if (v == null) return 0f;
        return v.setScale(1, RoundingMode.HALF_UP).floatValue();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        return Long.valueOf(o.toString());
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return Integer.valueOf(o.toString());
    }
}
