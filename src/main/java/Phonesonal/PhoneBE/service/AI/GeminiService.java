package Phonesonal.PhoneBE.service.AI;

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

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateFitnessGoals(int weight, int height) {
        double currentBMI = calculateBMI(weight, height);

        String prompt = String.format("""
        사용자 신체 정보를 바탕으로 건강한 목표 수치를 JSON 형식으로 생성해주세요.
        
        **현재 상태:**
        - 몸무게: %d kg
        - 키: %d cm  
        - 현재 BMI: %.1f
        
        **요구사항:**
        1. 건강하고 현실적인 3개월 목표 수치 설정
        2. 일반적인 성인 기준으로 적정 목표 설정
        3. 의학적으로 안전한 범위 내에서 설정
        
        **응답 형식 (JSON):**
        {
          "targetWeight": [목표 체중 숫자값],
          "targetBMI": [목표 BMI 숫자값],  
          "targetBodyFatPercentage": [목표 체지방률 숫자값],
          "targetSkeletalMuscleWeight": [목표 골격근량 숫자값],
          "recommendedCalories": [권장 일일 칼로리 숫자값],
          "workoutFrequency": [주간 운동 횟수 숫자값],
          "cardioMinutesPerWeek": [주간 유산소 시간(분) 숫자값],
          "strengthTrainingDays": [주간 근력운동 일수 숫자값]
        }
        
        숫자값만 포함하고 단위나 추가 텍스트는 제외해주세요.
        """, weight, height, currentBMI);


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
    private double calculateBMI(int weight, int height) {
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }
}