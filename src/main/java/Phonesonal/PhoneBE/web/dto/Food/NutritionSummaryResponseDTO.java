package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionSummaryResponseDTO {
    private LocalDate date;
    private MealSummary summary;

    private int plannedTotalCalorie; // RecommendMeal의 아침/점심/저녁/간식 전부(complete 여부 상관없음)
    private int actualTotalCalorie;  // RecommendMeal 중 complete + UserMeal 전부

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealSummary {
        private NutritionData BREAKFAST;
        private NutritionData LUNCH;
        private NutritionData SNACK;
        private NutritionData DINNER;
    }
}