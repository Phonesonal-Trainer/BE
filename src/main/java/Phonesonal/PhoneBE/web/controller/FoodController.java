package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Food.FoodQueryService;
import Phonesonal.PhoneBE.service.Food.MealQueryService;
import Phonesonal.PhoneBE.service.Food.FavoriteFoodCommandService;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponse;
import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Food", description = "음식 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/foods")
public class FoodController {

    private final FoodQueryService foodQueryService;
    private final FavoriteFoodCommandService favoriteFoodCommandService;
    private final MealQueryService mealQueryService;

    @Operation(summary = "기존 음식 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchFoodResponseDTO>>> searchFoods(
            @RequestParam String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<SearchFoodResponseDTO> result = foodQueryService.searchFoods(keyword, userId);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }

    @Operation(summary = "식단 즐겨찾기 토글")
    @PostMapping("/{foodId}/favorite")
    public ResponseEntity<ApiResponse<String>> toggleFavorite(
            @PathVariable Long foodId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        favoriteFoodCommandService.toggleFavorite(foodId, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "즐겨찾기 상태 변경"));
    }

    @GetMapping("/api/meals/nutrition-summary")
    public ResponseEntity<NutritionSummaryResponse> getNutritionSummary(
            @RequestParam Long userId,
            @RequestParam Long goalPeriodId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        NutritionSummaryResponse response = mealQueryService.getNutritionSummary(userId, goalPeriodId, date);
        return ResponseEntity.ok(response);
    }
}
