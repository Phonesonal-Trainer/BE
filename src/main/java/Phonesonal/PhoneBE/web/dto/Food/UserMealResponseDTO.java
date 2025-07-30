package Phonesonal.PhoneBE.web.dto.Food;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserMealResponseDTO {
    private Long recordId;
    private String foodName;
    private MealTime mealTime;
    private LocalDate date;
    private float quantity;
}
