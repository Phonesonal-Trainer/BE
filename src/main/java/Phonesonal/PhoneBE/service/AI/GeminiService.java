package Phonesonal.PhoneBE.service.AI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateFitnessGoals(BigDecimal weight, BigDecimal height, BigDecimal bodyFatRate, BigDecimal muscleMass) {
        BigDecimal currentBMI = calculateBMI(weight, height);

        // 현재 상태 정보 구성
        StringBuilder currentStatus = new StringBuilder();
        currentStatus.append(String.format("- 몸무게: %.1f kg\n", weight.doubleValue()));
        currentStatus.append(String.format("- 키: %.1f cm\n", height.doubleValue()));
        currentStatus.append(String.format("- 현재 BMI: %.1f\n", currentBMI));

        // 선택사항 추가
        if (bodyFatRate != null) {
            currentStatus.append(String.format("- 체지방률: %.1f%%\n", bodyFatRate));
        }
        if (muscleMass != null) {
            currentStatus.append(String.format("- 골격근량: %.1f kg\n", muscleMass));
        }

        String prompt = String.format("""
        SYSTEM: You are a JSON API. You must return ONLY valid JSON without any markdown formatting.
        사용자 신체 정보를 바탕으로 건강한 목표 수치를 JSON 형식으로 생성해주세요.
        
        **현재 상태:**
        %s
        
        **요구사항:**
        1. 건강하고 현실적인 3개월 목표 수치 설정
        2. 일반적인 성인 기준으로 적정 목표 설정
        3. 의학적으로 안전한 범위 내에서 설정
        %s
        
        **응답 형식 (JSON):**
        {
          "targetWeight": [목표 체중 숫자값],
          "targetBMI": [목표 BMI 숫자값],
          "targetBodyFatRate": [목표 체지방률 숫자값],
          "targetMuscleMass": [골격근량 변동사항 텍스트(증가, 소폭 증가, 감소, 소폭 감소, 유지)],
          "recommendedNutrition": [권장 주 영양소 텍스트(ex: 고단백/저지방)],
          "recommendedCalories": [권장 일일 칼로리 숫자값],
          "workoutFrequency": [주간 운동 횟수 숫자값],
          "cardioDaysPerWeek": [주간 유산소 일수 숫자값],
          "cardioMinutesPerWeek": [주간 유산소 시간(분) 숫자값],
          "strengthTrainingDays": [주간 근력운동(무산소) 일수 숫자값],
          "strengthTrainingTime": [주간 근력운동(무산소) 시간(분) 숫자값],
          "overallRecommendation": [진단 결과 한줄 요약]
        }
        
        지시된 숫자값, 텍스트만 포함하고 그 외의 단위나 추가 텍스트는 제외해주세요.
        **중요: 반드시 순수 JSON만 응답하세요. 마크다운 코드블록(```)이나 추가 설명 없이 JSON 객체만 반환해주세요.**
        """, currentStatus, generateAdditionalContext(bodyFatRate, muscleMass));


        // 요청 본문 구성
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        contents.put("parts", List.of(textPart));
        body.put("contents", List.of(contents));

        // 생성 설정 추가 (선택사항)
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1000);
        body.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + apiKey;
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url,
                    request,
                    Map.class
            );

            if (response.getBody() == null) {
                return "목표 생성 실패: 응답이 비어있습니다.";
            }

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "목표 생성 실패: Gemini 응답에 후보가 없습니다.";
            }

            Map candidate = candidates.get(0);
            Map content = (Map) candidate.get("content");
            if (content == null) {
                return "목표 생성 실패: 컨텐츠가 없습니다.";
            }

            List<Map> responseParts = (List<Map>) content.get("parts");
            if (responseParts == null || responseParts.isEmpty()) {
                return "목표 생성 실패: 텍스트 부분이 없습니다.";
            }

            return (String) responseParts.get(0).get("text");

        } catch (Exception e) {
            return "목표 생성 실패: " + e.getMessage();
        }
    }

    // 간단한 BMI 계산 헬퍼 메서드
    public BigDecimal calculateBMI(BigDecimal weight, BigDecimal height) {
        BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return weight.divide(heightInMeters.multiply(heightInMeters), 2, RoundingMode.HALF_UP);
    }

    // 추가 컨텍스트 생성 헬퍼 메서드
    private String generateAdditionalContext(BigDecimal bodyFatRate, BigDecimal muscleMass) {
        StringBuilder context = new StringBuilder();

        if (bodyFatRate != null || muscleMass != null) {
            context.append("4. 제공된 체성분 정보를 활용하여 더 정확한 목표 설정\n");

            if (bodyFatRate != null) {
                context.append("   - 현재 체지방률을 고려한 목표 체지방률 설정\n");
            }
            if (muscleMass != null) {
                context.append("   - 현재 골격근량을 고려한 목표 근육량 설정\n");
            }
        }

        return context.toString();
    }
}