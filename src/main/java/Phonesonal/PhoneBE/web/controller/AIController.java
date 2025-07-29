package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.GeminiService;
import Phonesonal.PhoneBE.service.User.DiagnosisService;
import Phonesonal.PhoneBE.web.dto.DiagnosisResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/fitness")
@AllArgsConstructor
public class AIController {

    private final GeminiService geminiService;
    private final DiagnosisService diagnosisService;

    @PostMapping("/goals")
    @Operation(summary = "목표 진단결과 조회 API", description = "목표 진단결과 조회")
    public ApiResponse<DiagnosisResultDTO> generateGoals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        String result = geminiService.generateFitnessGoals(
                user.getWeight(),
                user.getHeight(),
                user.getBodyFatRate(),
                user.getMuscleMass()
        );

        // 🔍 이 부분을 추가!
        System.out.println("=== Gemini 원본 응답 ===");
        System.out.println("응답 길이: " + result.length());
        System.out.println("첫 10글자: [" + (result.length() > 10 ? result.substring(0, 10) : result) + "]");
        System.out.println("전체 응답:");
        System.out.println(result);
        System.out.println("===================");

        // 🛠️ 백틱 제거 및 JSON 정리
        String cleanJson = result;

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

        System.out.println("=== 정리된 JSON ===");
        System.out.println(cleanJson);
        System.out.println("================");

        // JSON 파싱
        DiagnosisResultDTO response;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(cleanJson);

            // Diagnosis 테이블에 저장
            Diagnosis diagnosis = Diagnosis.builder()
                    .targetWeight(new BigDecimal(jsonNode.get("targetWeight").asText()))
                    .targetBMI(new BigDecimal(jsonNode.get("targetBMI").asText()))
                    .targetMuscleMass(jsonNode.get("targetMuscleMass").asText())
                    .targetBodyFatRate(new BigDecimal(jsonNode.get("targetBodyFatRate").asText()))
                    .recommendedNutrition(jsonNode.get("recommendedNutrition").asText()) // "고단백", "저탄수화물" 등
                    .recommendedCalories(jsonNode.get("recommendedCalories").asInt())
                    .workoutFrequency(jsonNode.get("workoutFrequency").asInt())
                    .cardioDaysPerWeek(jsonNode.get("cardioDaysPerWeek").asInt())
                    .cardioMinutesPerWeek(jsonNode.get("cardioMinutesPerWeek").asInt())
                    .strengthTrainingDays(jsonNode.get("strengthTrainingDays").asInt())
                    .strengthTrainingTime(jsonNode.get("strengthTrainingTime").asInt())
                    .overallRecommendation(jsonNode.get("overallRecommendation").asText()) // 진단 결과 한줄 요약
                    .user(user)
                    .build();

            diagnosisService.saveDiagnosis(diagnosis);

            response = DiagnosisResultDTO.builder()
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

        } catch (Exception e) {
            // 상세한 에러 정보 출력
            System.out.println("=== 에러 상세 정보 ===");
            System.out.println("에러 타입: " + e.getClass().getSimpleName());
            System.out.println("에러 메시지: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("근본 원인: " + e.getCause().getMessage());
            }
            e.printStackTrace(); // 전체 스택 트레이스 출력
            System.out.println("==================");

            // 원래 예외를 그대로 던지기
            throw new RuntimeException("진단 결과 저장 실패: " + e.getMessage(), e);
        }

        return ApiResponse.onSuccess(response);
    }
}