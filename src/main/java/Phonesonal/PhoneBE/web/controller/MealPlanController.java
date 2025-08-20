package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.GeminiMealService;
import Phonesonal.PhoneBE.service.Food.RecommendMealCommandService;
import Phonesonal.PhoneBE.service.Food.RecommendMealQueryService;
import Phonesonal.PhoneBE.web.dto.Food.CompleteStatusResponseDTO;
import Phonesonal.PhoneBE.web.dto.Food.GenerateMealRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.RecommendMealResponseDTO;
import Phonesonal.PhoneBE.service.Food.MealRecommendationService;
import Phonesonal.PhoneBE.web.dto.Food.UpdateCompleteStatusRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "MealPlan", description = "식단 플랜 관련 API")
@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
// 식단 플랜 관련
public class MealPlanController {

    private final RecommendMealQueryService recommendMealQueryService;
    private final RecommendMealCommandService recommendMealCommandService;
    private final GeminiMealService geminiMealService;
    private final MealRecommendationService mealRecommendationService;


    @Operation(summary = "식단 플랜 조회")
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<RecommendMealResponseDTO>>> getMealPlans(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("mealTime") MealTime mealTime,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

                Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();

        List<RecommendMealResponseDTO> plans = recommendMealQueryService.getMealPlans(
                goalPeriodId,
                date,
                mealTime
        );

        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, plans));
    }

    @Operation(summary = "식단 체크 상태 수정")
    @PatchMapping("/plans/complete")
    public ResponseEntity<ApiResponse<CompleteStatusResponseDTO>> updateCompleteStatus(
            @RequestBody UpdateCompleteStatusRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();
        CompleteStatusResponseDTO result =
                recommendMealCommandService.updateCompleteStatus(request, userId, goalPeriodId); 

        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }

    @Operation(summary = "식단 플랜 생성")
    @PostMapping("/plans/generate")
    public ResponseEntity<ApiResponse<Integer>> generateWeeklyMeals(
            @RequestBody @Valid GenerateMealRequestDTO req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Diagnosis diagnosis = user.getDiagnosis();

        int saved = geminiMealService.generateAndSaveWeeklyAllMeals(user, diagnosis, req);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, saved));
    }

    @Operation(summary = "식단 플랜 재생성 (피드백 반영)")
    @PostMapping("/plans/regenerate")
    public ResponseEntity<ApiResponse<String>> regenerateWeeklyMeals(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        mealRecommendationService.regenerateNextWeekByFeedback(userId);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "SUCCESS"));
    }
}