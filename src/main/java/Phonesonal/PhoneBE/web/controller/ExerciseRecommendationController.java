package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Exercise.ExerciseRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/exercise-recommendation")
@Tag(name = "Exercise Recommendation", description = "AI 운동 추천 관련 API")
@RequiredArgsConstructor
public class ExerciseRecommendationController {

    private final ExerciseRecommendationService exerciseRecommendationService;

    @PostMapping("/generate")
    @Operation(summary = "운동 추천 생성 API")
    public ApiResponse<String> generateRecommendation(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        exerciseRecommendationService.generateInitialWeeklyRecommendation(userId);

        return ApiResponse.onSuccess("SUCCESS");
    }

    @PostMapping("/regenerate")
    @Operation(summary = "운동 재추천 API")
    public ApiResponse<String> regenerateRecommendation(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        exerciseRecommendationService.regenerateCurrentWeekRecommendation(userId); // 기존 메서드 사용

        return ApiResponse.onSuccess("SUCCESS");
    }
}
