package Phonesonal.PhoneBE.service.AI;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiExerciseServiceImpl implements GeminiExerciseService {

    @Value("${gemini.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String generateWeeklyExerciseRecommendation(User user, Diagnosis diagnosis,
                                                       List<Exercise> availableExercises,
                                                       List<Long> excludeExerciseIds) {

        String prompt = buildExerciseRecommendationPrompt(user, diagnosis, availableExercises, excludeExerciseIds);
        return callGeminiWithRetry(prompt, 3);
    }

    /**
     * 운동 추천 프롬프트 생성
     */
    private String buildExerciseRecommendationPrompt(User user, Diagnosis diagnosis,
                                                     List<Exercise> availableExercises,
                                                     List<Long> excludeExerciseIds) {

        StringBuilder userInfo = new StringBuilder();
        userInfo.append(String.format("- 체중: %.1f kg\n", user.getWeight().doubleValue()));
        userInfo.append(String.format("- 키: %.1f cm\n", user.getHeight().doubleValue()));
        userInfo.append(String.format("- 나이: %d세\n", user.getAge()));
        userInfo.append(String.format("- 성별: %s\n", user.getGender().name()));
        userInfo.append(String.format("- 목표: %s\n", user.getPurpose().name()));

        if (user.getBodyFatRate() != null) {
            userInfo.append(String.format("- 체지방률: %.1f%%\n", user.getBodyFatRate()));
        }
        if (user.getMuscleMass() != null) {
            userInfo.append(String.format("- 골격근량: %.1f kg\n", user.getMuscleMass()));
        }

        StringBuilder diagnosisInfo = new StringBuilder();
        diagnosisInfo.append(String.format("- 주간 운동 횟수: %d일\n", diagnosis.getWorkoutFrequency()));
        diagnosisInfo.append(String.format("- 주간 무산소 운동: %d일, %d분\n",
                diagnosis.getStrengthTrainingDays(), diagnosis.getStrengthTrainingTime()));
        diagnosisInfo.append(String.format("- 주간 유산소 운동: %d일, %d분\n",
                diagnosis.getCardioDaysPerWeek(), diagnosis.getCardioMinutesPerWeek()));

        StringBuilder exerciseList = new StringBuilder();
        exerciseList.append("[\n");
        for (Exercise exercise : availableExercises) {
            exerciseList.append(String.format(
                    "  {\"id\": %d, \"name\": \"%s\", \"type\": \"%s\", \"bodyParts\": [%s], \"defaultSets\": %d, \"defaultReps\": %d},\n",
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getType().name(),
                    exercise.getBodyParts().stream()
                            .map(ebp -> "\"" + ebp.getBodyPart().getBodyCategory().name() + "\"")
                            .collect(Collectors.joining(", ")),
                    exercise.getDefaultSet() != null ? exercise.getDefaultSet() : 3,
                    exercise.getDefaultCount() != null ? exercise.getDefaultCount() : 12
            ));
        }
        if (exerciseList.length() > 2) {
            exerciseList.setLength(exerciseList.length() - 2);
        }
        exerciseList.append("\n]");

        String excludeInfo = "";
        if (excludeExerciseIds != null && !excludeExerciseIds.isEmpty()) {
            excludeInfo = "\n**금지할 운동들:** " +
                    excludeExerciseIds.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }

        return String.format("""
        SYSTEM: You are a JSON API. You must return ONLY valid JSON without any markdown formatting.
        
        사용자 정보를 바탕으로 7일간 운동 계획을 JSON 형식으로 생성해주세요.
        
        **사용자 정보:**
        %s
        
        **진단 결과:**
        %s
        
        **사용 가능한 운동 목록:**
        %s
        %s
        
        **요구사항:**
        1. 7일 계획 생성 (day1~day7)
        2. 각 날짜별로 서로 다른 주요 부위 집중 (chest, legs, back, arms, shoulder)
        3. 제공된 운동 ID만 사용
        4. 무산소/유산소 비율 준수
        5. 같은 운동 반복 최소화
        6. 초보자도 할 수 있는 적절한 강도
        7. 일요일은 휴식 또는 가벼운 유산소
        
        **응답 형식 (JSON):**
        {
          "day1": [
            {"exerciseId": 1, "sets": 3, "reps": 12, "weight": 10},
            {"exerciseId": 5, "sets": 3, "reps": 10, "weight": 15}
          ],
          "day2": [
            {"exerciseId": 3, "sets": 4, "reps": 8, "weight": 20}
          ],
          "day3": [...],
          "day4": [...],
          "day5": [...],
          "day6": [...],
          "day7": []
        }
        
        **중요: 반드시 순수 JSON만 응답하세요.**
        """, userInfo, diagnosisInfo, exerciseList, excludeInfo);
    }

    /**
     * Gemini API 호출 with 재시도
     */
    private String callGeminiWithRetry(String prompt, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                String response = callGeminiAPI(prompt);
                return validateAndCleanResponse(response);
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    log.error("Gemini API 호출 최종 실패", e);
                    throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
                }

                try {
                    Thread.sleep(1000 * (i + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
                }
            }
        }
        throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
    }

    /**
     * Gemini API 호출
     */
    private String callGeminiAPI(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + apiKey;

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        contents.put("parts", List.of(textPart));
        body.put("contents", List.of(contents));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 2000);
        body.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() == null) {
                throw new CommonExceptionHandler(ErrorStatus.AI_API_EMPTY_RESPONSE);
            }

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_CANDIDATES);
            }

            Map candidate = candidates.get(0);
            Map content = (Map) candidate.get("content");
            if (content == null) {
                throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_CONTENT);
            }

            List<Map> responseParts = (List<Map>) content.get("parts");
            if (responseParts == null || responseParts.isEmpty()) {
                throw new CommonExceptionHandler(ErrorStatus.AI_API_NO_PARTS);
            }

            return (String) responseParts.get(0).get("text");

        } catch (Exception e) {
            throw new CommonExceptionHandler(ErrorStatus.AI_API_CALL_FAILED);
        }
    }

    /**
     * 응답 검증 및 정제
     */
    private String validateAndCleanResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new CommonExceptionHandler(ErrorStatus.AI_API_EMPTY_RESPONSE);
        }

        String cleanJson = cleanJsonResponse(response);

        try {
            objectMapper.readValue(cleanJson, Map.class);
            return cleanJson;
        } catch (Exception e) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }
    }

    /**
     * JSON 응답 정제
     */
    private String cleanJsonResponse(String geminiResponse) {
        String cleanJson = geminiResponse;

        cleanJson = cleanJson.replaceAll("```[a-zA-Z]*", "");
        cleanJson = cleanJson.replaceAll("```", "");

        int start = cleanJson.indexOf('{');
        int end = cleanJson.lastIndexOf('}');

        if (start != -1 && end != -1 && start < end) {
            cleanJson = cleanJson.substring(start, end + 1);
        } else {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }

        return cleanJson;
    }

}
