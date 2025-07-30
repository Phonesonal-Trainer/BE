package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;

public interface UserMealCommandService {
    UserMealResponseDTO addUserMealFromFood(AddUserMealFromFoodRequestDTO dto, Long goalPeriodId);
}
