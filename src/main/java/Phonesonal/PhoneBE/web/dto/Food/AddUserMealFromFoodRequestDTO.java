package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddUserMealFromFoodRequestDTO {
    private Long foodId;
    private Long goalPeriodId;
    private LocalDate date;
    private MealTime mealTime;
    private float quantity;
}
