package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercise-recommendation")
@Tag(name = "Exercise Recommendation", description = "운동 추천 관련 API")
@RequiredArgsConstructor
public class ExerciseRecommendationController {

    @GetMapping("/current")
    @Operation(summary = "현재 주차 운동 추천 조회 API", description = "현재 주차의 운동 추천을 조회합니다. 없으면 자동 생성")
    public ApiResponse<ExerciseRecmmendationResponseDTO> getCurrentRecommendation(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        User user = userDetails.getUser();

        // 현재 주차 운동 추천(없으면 자동 생성)
        WeeklyRecoomendation recommendation = exerciseRecommendationService.getCurrentWeeklyRecommendation(user);

        // JSON을 DTO로 변환
        ExerciseRecommendationResponseDTO response = exerciseRecommendationConverter
                .convertToResponseDTO(recommendation.getGeminiResponseJson());

        return ApiResponse.onSuccess(response);
    }
}
