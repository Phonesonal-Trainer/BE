package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddUserMealCustomRequestDTO {
    private String name;
    //private Float quantity;
    private Float calorie;
    private Float carb;
    private Float protein;
    private Float fat;
    private LocalDate date;
    private MealTime mealTime;
}

