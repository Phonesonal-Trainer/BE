package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.RecommendMealService.RecommendMealCommandService;
import Phonesonal.PhoneBE.service.RecommendMealService.RecommendMealQueryService;
import Phonesonal.PhoneBE.web.dto.CompleteStatusResponseDTO;
import Phonesonal.PhoneBE.web.dto.RecommendMealRequestDTO;
import Phonesonal.PhoneBE.web.dto.RecommendMealResponseDTO;
import Phonesonal.PhoneBE.web.dto.UpdateCompleteStatusRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
// 식단 플랜 관련
public class MealPlanController {

    private final RecommendMealQueryService recommendMealQueryService;
    private final RecommendMealCommandService recommendMealCommandService;


    @Operation(summary = "식단 플랜 조회")
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<RecommendMealResponseDTO>>> getMealPlans(
            @ModelAttribute RecommendMealRequestDTO.GetMealPlanRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails){
                //Long userId = userDetails.getUser().getId();
                Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        List<RecommendMealResponseDTO> plans = recommendMealQueryService.getMealPlans(
                goalPeriodId,
                request.getDate(),
                request.getMealTime()
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
        CompleteStatusResponseDTO result = recommendMealCommandService.updateCompleteStatus(request, userId);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }
}