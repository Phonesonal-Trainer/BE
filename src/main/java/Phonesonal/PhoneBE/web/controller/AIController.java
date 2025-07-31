package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.converter.DiagnosisConverter;
import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.GeminiService;
import Phonesonal.PhoneBE.service.User.DiagnosisService;
import Phonesonal.PhoneBE.web.dto.DiagnosisResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diagnosis")
@Tag(name = "Diagnosis", description = "추천 진단 관련 API")
@RequiredArgsConstructor
public class AIController {

    private final GeminiService geminiService;
    private final DiagnosisService diagnosisService;
    private final DiagnosisConverter diagnosisConverter;

    @PostMapping("/goals")
    @Operation(summary = "목표 진단결과 조회 API", description = "목표 진단결과 조회")
    public ApiResponse<DiagnosisResultDTO> generateGoals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();

        // 1. Gemini AI로부터 응답 받기
        String geminiResponse = geminiService.generateFitnessGoals(
                user.getWeight(),
                user.getHeight(),
                user.getBodyFatRate(),
                user.getMuscleMass(),
                user.getDeadline()
        );

        // 2. Gemini 응답을 Diagnosis 엔티티로 변환
        Diagnosis diagnosis = diagnosisConverter.convertGeminiResponseToDiagnosis(geminiResponse, user);

        // 3. 진단 결과 저장
        diagnosisService.saveDiagnosis(diagnosis);

        // 4. 응답 DTO로 변환
        DiagnosisResultDTO response = diagnosisConverter.convertToResponseDTO(diagnosis, user);

        return ApiResponse.onSuccess(response);
    }
}