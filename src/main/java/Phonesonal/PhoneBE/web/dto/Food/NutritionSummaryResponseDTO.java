package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class NutritionSummaryResponseDTO {
    private LocalDate date;
    private MealSummary summary;

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