package Phonesonal.PhoneBE.web.dto;

import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class RecommendMealResponseDTO {

    private Long foodId;
    private String foodName;
    private MealTime mealTime;
    private LocalDate date;
    private Float quantity;
    private String complete;
    private Integer weekNumber;
}

