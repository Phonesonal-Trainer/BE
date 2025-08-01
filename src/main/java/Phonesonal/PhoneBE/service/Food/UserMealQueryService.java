package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface UserMealQueryService {
    List<UserMealResponseDTO> getUserMeals(Long goalPeriodId, LocalDate date, MealTime mealTime);
}
