package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Food.FoodQueryService;
import Phonesonal.PhoneBE.service.Food.MealQueryService;
import Phonesonal.PhoneBE.service.Food.FavoriteFoodCommandService;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponseDTO;
import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;
import Phonesonal.PhoneBE.web.dto.Food.ToggleFavoriteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @Parameter(
                    description = "정렬 기준",
                    schema = @Schema(allowableValues = {"popular", "favorite"})
            )
            @RequestParam(defaultValue = "popular") String sort, // 디폴트 빈도순
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<SearchFoodResponseDTO> result = foodQueryService.searchFoods(keyword, userId, sort);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }

    @Operation(summary = "식단 즐겨찾기 상태 변경")
    @PostMapping("/{foodId}/favorite")
    public ResponseEntity<ApiResponse<ToggleFavoriteResponseDTO>> toggleFavorite(
            @PathVariable Long foodId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ToggleFavoriteResponseDTO result =
                favoriteFoodCommandService.toggleFavorite(foodId, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }

    @Operation(summary = "하루 식단의 영양소 총합 조회 (식사별 조회)")
    @GetMapping("/nutrition-summary")
    public ResponseEntity<NutritionSummaryResponseDTO> getNutritionSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();

        NutritionSummaryResponseDTO response = mealQueryService.getNutritionSummary(userId, goalPeriodId, date);
        return ResponseEntity.ok(response);
    }
}
