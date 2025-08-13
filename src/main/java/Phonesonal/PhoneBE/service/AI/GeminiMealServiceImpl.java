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

    @Override
    public String generateWeeklyAllMeals(User user,
                                         Diagnosis diagnosis,
                                         List<Food> availableFoods) {
        String prompt = buildAllMealsPrompt(user, diagnosis, availableFoods);
        return callGeminiWithRetry(prompt, 3);
    }

    @Transactional
    @Override
    public int generateAndSaveWeeklyAllMeals(User user,
                                             Diagnosis diagnosis,
                                             GenerateMealRequestDTO req) {
        // 1) 후보 음식: is_custom=false
        List<Food> available = foodRepository.findByIsCustomFalse();
        if (available.isEmpty()) throw new IllegalArgumentException("is_custom=false 음식 없음.");

        // 1-1) id -> Food 맵은 딱 한 번 생성
        Map<Long, Food> foodMap = available.stream()
                .collect(Collectors.toMap(Food::getFoodId, f -> f, (a, b) -> a));

        // 2) Gemini 호출(한 번에 7×4 생성)
        String json = generateWeeklyAllMeals(user, diagnosis, available);

        // 3) 파싱
        Map<String, Object> root = readAs(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

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
            LocalDate date = start.plusDays(d - 1);

            Map<String, Object> dayObj = safeGetMap(root.get(dayKey));
            if (dayObj == null) {
                log.warn("응답에 {} 없음. 스킵.", dayKey);
                continue;
            }

            for (MealTime mt : EnumSet.of(MealTime.BREAKFAST, MealTime.LUNCH, MealTime.DINNER, MealTime.SNACK)) {
                Object raw = dayObj.get(mt.name()); // 대문자 우선
                if (raw == null) raw = dayObj.get(mt.name().toLowerCase(Locale.ROOT)); // 혹시 대비
                List<Map<String, Object>> items = safeGetListOfMap(raw);
                if (items == null || items.isEmpty()) {
                    log.warn("{}-{} 항목 없음.", dayKey, mt);
                    continue;
                }

                // 중복 foodId 제거 후 정확히 3개만 사용
                Set<Long> seen = new HashSet<>();
                List<Map<String, Object>> uniq = new ArrayList<>(3);
                for (Map<String, Object> m : items) {
                    Long fid = toLong(m.get("foodId"));
                    if (fid != null && seen.add(fid)) {
                        uniq.add(m);
                        if (uniq.size() == 3) break;
                    }
                }
                if (uniq.size() != 3) {
                    log.warn("{}-{} 중복 제거 후 3개 미만. 스킵.", dayKey, mt);
                    continue;
                }

                for (Map<String, Object> item : uniq) {
                    Long foodId = toLong(item.get("foodId"));
                    Integer grams = toInt(item.get("grams"));

                    Food food = foodMap.get(foodId);
                    if (food == null) {
                        throw new IllegalArgumentException("유효하지 않은 foodId: " + foodId);
                    }

                    RecommendMeal rm = RecommendMeal.builder()
                            // complete은 @PrePersist로 INCOMPLETE 자동 적용
                            .date(date)
                            .mealTime(mt)
                            .quantity(grams == null ? null : grams.floatValue()) // g 단위만 저장
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
        userInfo.append(String.format("- 나이: %d세%n", user.getAge())); // ← null 체크 제거

        if (user.getGender() != null) userInfo.append(String.format("- 성별: %s%n", user.getGender().name()));
        if (user.getPurpose() != null) userInfo.append(String.format("- 목표: %s%n", user.getPurpose().name()));
        if (user.getBodyFatRate() != null) //bigdecimal 포맷이라
            userInfo.append(String.format("- 체지방률: %.1f%%%n", as1(user.getBodyFatRate())));
        if (user.getMuscleMass() != null)
            userInfo.append(String.format("- 골격근량: %.1f kg%n", as1(user.getMuscleMass())));


        StringBuilder diag = new StringBuilder();
        if (diagnosis != null) {
            diag.append(String.format("- 권장 칼로리: %d kcal/day%n", diagnosis.getRecommendedCalories()));
            diag.append(String.format("- 권장 영양소 성향: %s%n", diagnosis.getRecommendedNutrition()));
            diag.append(String.format("- 주간 운동 빈도: %d일%n", diagnosis.getWorkoutFrequency()));
        }

        // AI는 이 id/name만 보고 조합한다. 매크로는 AI가 산출.
        String foodsJson = availableFoods.stream()
                .map(f -> String.format("{\"id\": %d, \"name\": \"%s\"}",
                        f.getFoodId(), escape(f.getName())))
                .collect(Collectors.joining(", "));

        String kcalGuide = """
        아침 25%%, 점심 35%%, 저녁 30%%, 간식 10%% 분배를 권장하되, +-10%% 내 조정 가능.
        """;

        return String.format("""
SYSTEM: You are a JSON API. Return ONLY valid JSON. No markdown.

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
1) JSON 최상위는 day1..day7 키
2) 각 day는 { "BREAKFAST": [...], "LUNCH": [...], "DINNER": [...], "SNACK": [...] }
3) 각 리스트는 정확히 3개 아이템
4) 아이템 스키마:
   { "foodId": number, "grams": number, "calorie": number, "carb": number, "protein": number, "fat": number }
5) grams는 g 기준, calorie/carb/protein/fat는 grams 기준의 값
6) 하루 총칼로리가 과도하게 벗어나지 않도록 구성(진단 칼로리 참고)
7) **키는 반드시 대문자 enum 이름과 정확히 일치해야 함: BREAKFAST, LUNCH, DINNER, SNACK**
8) 반드시 순수 JSON만 반환
""", userInfo, diag, foodsJson, kcalGuide);
    }

    /* ===================== Gemini Call ===================== */

    private String callGeminiWithRetry(String prompt, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                String response = callGeminiAPI(prompt);
                return validateAndClean(response);
            } catch (Exception e) {
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

    private String callGeminiAPI(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + apiKey;

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", List.of(Map.of("text", prompt)));
        body.put("contents", List.of(contents));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.4);
        generationConfig.put("maxOutputTokens", 2400);
        body.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> res = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
        Map resBody = res.getBody();
        if (resBody == null) throw new CommonExceptionHandler(ErrorStatus.AI_API_EMPTY_RESPONSE);

        List<Map> candidates = (List<Map>) resBody.get("candidates");
        if (candidates == null || candidates.isEmpty()) throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_CANDIDATES);

        Map content = (Map) candidates.get(0).get("content");
        if (content == null) throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_CONTENT);

        List<Map> parts = (List<Map>) content.get("parts");
        if (parts == null || parts.isEmpty()) throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_PARTS);

        return (String) parts.get(0).get("text");
    }

    private String validateAndClean(String response) {
        if (response == null || response.trim().isEmpty())
            throw new CommonExceptionHandler(ErrorStatus.AI_API_EMPTY_RESPONSE);

        String clean = stripToJson(response);
        try {
            objectMapper.readValue(clean, Map.class);
            return clean;
        } catch (Exception e) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }
    }

    private String stripToJson(String s) {
        String t = s.replaceAll("```[a-zA-Z]*", "").replaceAll("```", "");
        int a = t.indexOf('{');
        int b = t.lastIndexOf('}');
        if (a == -1 || b == -1 || a >= b) throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        return t.substring(a, b + 1).trim();
    }

    /* ===================== Helpers ===================== */

    private GoalPeriod resolveGoalPeriod(User user) {
        GoalPeriod gp = user.getGoalPeriod();
        if (gp == null) {
            throw new IllegalStateException("사용자의 GoalPeriod를 찾을 수 없습니다.");
        }
        return gp;
    }

    private Map<String, Object> safeGetMap(Object o) {
        if (o instanceof Map) return (Map<String, Object>) o;
        return null;
    }

    private List<Map<String, Object>> safeGetListOfMap(Object o) {
        if (o instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object e : list) if (e instanceof Map) out.add((Map<String, Object>) e);
            return out;
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
