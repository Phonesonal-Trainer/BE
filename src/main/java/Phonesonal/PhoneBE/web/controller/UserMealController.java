package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Food.UserMealCommandService;
import Phonesonal.PhoneBE.service.Food.UserMealQueryService;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealCustomRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;
import Phonesonal.PhoneBE.web.dto.Food.UpdateUserMealQuantityRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "UserMeal", description = "추가 식단 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-meals")
public class UserMealController {

    private final UserMealCommandService userMealCommandService;

    @Operation(summary = "기존 음식 검색 기반 추가 식단 업로드")
    @PostMapping("/from-food")
    public ResponseEntity<ApiResponse<UserMealResponseDTO>> addUserMealFromFood(
            @RequestBody AddUserMealFromFoodRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        UserMealResponseDTO result = userMealCommandService.addUserMealFromFood(requestDTO, goalPeriodId);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }

    @Operation(summary = "직접 입력한 식단으로 추가 식단 업로드")
    @PostMapping("/custom")
    public ResponseEntity<ApiResponse<String>> addUserMealCustom(
            @RequestBody AddUserMealCustomRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        userMealCommandService.addUserMealCustom(requestDTO, userId, goalPeriodId);

        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "저장 완료"));
    }

    private final UserMealQueryService userMealQueryService;

    @Operation(summary = "추가 식단 전체 조회 (직접 입력 + 기존 음식 기반)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserMealResponseDTO>>> getUserMeals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam MealTime mealTime,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        List<UserMealResponseDTO> result = userMealQueryService.getUserMeals(goalPeriodId, date, mealTime);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }

    @Operation(summary = "추가 식단 양 수정 (검색 기반 음식만 가능)")
    @PatchMapping("/{recordId}")
    public ResponseEntity<ApiResponse<String>> updateUserMealQuantity(
            @PathVariable Long recordId,
            @RequestBody UpdateUserMealQuantityRequestDTO requestDTO
    ) {
        userMealCommandService.updateQuantity(recordId, requestDTO.getQuantity());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "수정 완료"));
    }

    @Operation(summary = "추가 식단 기록 삭제")
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<String>> deleteUserMeal(
            @PathVariable Long recordId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        userMealCommandService.deleteUserMeal(recordId, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "삭제 완료"));
    }


}