package Phonesonal.PhoneBE.converter;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.service.AI.GeminiService;
import Phonesonal.PhoneBE.web.dto.DiagnosisResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DiagnosisConverter {

    private final GeminiService geminiService;

    /**
     * Gemini AI 응답을 Diagnosis 엔티티로 변환
     */
    public Diagnosis convertGeminiResponseToDiagnosis(String geminiResponse, User user) {
        String cleanJson = cleanJsonResponse(geminiResponse);

        System.out.println("=== 정리된 JSON ===");
        System.out.println(cleanJson);
        System.out.println("================");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(cleanJson);

            // null 체크를 위한 헬퍼 메서드 사용
            BigDecimal targetBodyFatRate = getBigDecimalOrNull(jsonNode, "targetBodyFatRate");
            BigDecimal targetMuscleMass = getBigDecimalOrNull(jsonNode, "targetMuscleMass");

            return Diagnosis.builder()
                    .targetWeight(new BigDecimal(jsonNode.get("targetWeight").asText()))
                    .targetBMI(new BigDecimal(jsonNode.get("targetBMI").asText()))
                    .targetMuscleMass(targetMuscleMass)
                    .targetBodyFatRate(targetBodyFatRate)
                    .recommendedNutrition(jsonNode.get("recommendedNutrition").asText())
                    .recommendedCalories(jsonNode.get("recommendedCalories").asInt())
                    .workoutFrequency(jsonNode.get("workoutFrequency").asInt())
                    .cardioDaysPerWeek(jsonNode.get("cardioDaysPerWeek").asInt())
                    .cardioMinutesPerWeek(jsonNode.get("cardioMinutesPerWeek").asInt())
                    .strengthTrainingDays(jsonNode.get("strengthTrainingDays").asInt())
                    .strengthTrainingTime(jsonNode.get("strengthTrainingTime").asInt())
                    .overallRecommendation(jsonNode.get("overallRecommendation").asText())
                    .user(user)
                    .build();

        } catch (Exception e) {
            System.out.println("=== 에러 상세 정보 ===");
            System.out.println("에러 타입: " + e.getClass().getSimpleName());
            System.out.println("에러 메시지: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("근본 원인: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            System.out.println("==================");

            throw new RuntimeException("진단 결과 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Diagnosis 엔티티를 DiagnosisResultDTO로 변환
     */
    public DiagnosisResultDTO convertToResponseDTO(Diagnosis diagnosis, User user) {
        return DiagnosisResultDTO.builder()
                .weight(user.getWeight())
                .targetWeight(diagnosis.getTargetWeight())
                .BMI(geminiService.calculateBMI(user.getWeight(), user.getHeight()))
                .targetBMI(diagnosis.getTargetBMI())
                .targetMuscleMass(diagnosis.getTargetMuscleMass())
                .bodyFatRate(user.getBodyFatRate())
                .targetBodyFatRate(diagnosis.getTargetBodyFatRate())
                .recommendedNutrition(diagnosis.getRecommendedNutrition())
                .recommendedCalories(diagnosis.getRecommendedCalories())
                .workoutFrequency(diagnosis.getWorkoutFrequency())
                .cardioDaysPerWeek(diagnosis.getCardioDaysPerWeek())
                .cardioMinutesPerWeek(diagnosis.getCardioMinutesPerWeek())
                .strengthTrainingDays(diagnosis.getStrengthTrainingDays())
                .strengthTrainingTime(diagnosis.getStrengthTrainingTime())
                .overallRecommendation(diagnosis.getOverallRecommendation())
                .build();
    }

    /**
     * Gemini 응답에서 JSON 부분만 정리해서 추출
     */
    private String cleanJsonResponse(String geminiResponse) {
        System.out.println("=== Gemini 원본 응답 ===");
        System.out.println("응답 길이: " + geminiResponse.length());
        System.out.println("첫 10글자: [" + (geminiResponse.length() > 10 ? geminiResponse.substring(0, 10) : geminiResponse) + "]");
        System.out.println("전체 응답:");
        System.out.println(geminiResponse);
        System.out.println("===================");

        String cleanJson = geminiResponse;

        // 1. 모든 백틱 제거
        cleanJson = cleanJson.replaceAll("```[a-zA-Z]*", "");  // ```json 제거
        cleanJson = cleanJson.replaceAll("```", "");            // 남은 ``` 제거

        // 2. { 부터 } 까지만 추출 (더 안전)
        int start = cleanJson.indexOf('{');
        int end = cleanJson.lastIndexOf('}');

        if (start != -1 && end != -1 && start < end) {
            cleanJson = cleanJson.substring(start, end + 1);
        } else {
            throw new RuntimeException("JSON 형식을 찾을 수 없습니다: " + cleanJson);
        }

        return cleanJson;
    }

    /**
     * JsonNode에서 BigDecimal 값을 안전하게 추출 (null 허용)
     */
    private BigDecimal getBigDecimalOrNull(JsonNode jsonNode, String fieldName) {
        if (jsonNode.has(fieldName) && !jsonNode.get(fieldName).isNull()) {
            return new BigDecimal(jsonNode.get(fieldName).asText());
        }
        return null;
    }

    /**
     * JsonNode에서 String 값을 안전하게 추출 (null 허용)
     */
    private String getStringOrNull(JsonNode jsonNode, String fieldName) {
        if (jsonNode.has(fieldName) && !jsonNode.get(fieldName).isNull()) {
            return jsonNode.get(fieldName).asText();
        }
        return null;
    }
}