package Phonesonal.PhoneBE.web.dto;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class RecommendMealRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetMealPlanRequestDTO {
        private Long userId;
        private MealTime mealTime; // "BREAKFAST", "LUNCH", "DINNER", "SNACK"
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;
    }
}