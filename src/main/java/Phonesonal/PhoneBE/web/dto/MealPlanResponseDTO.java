package Phonesonal.PhoneBE.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MealPlanResponseDTO {
    private Long foodId;
    private String mealTime;
    private LocalDate date;
    private Float quantity;
    private String complete;

    public static MealPlanResponseDTO from(RecommendMeal entity) {
        return new MealPlanResponseDTO(
                entity.getFoodId(),
                entity.getMealTime().name(),
                entity.getDate(),
                entity.getQuantity(),
                entity.getComplete().name()
        );
    }
}
