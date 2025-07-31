package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.Food.AddUserMealCustomRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;

public interface UserMealCommandService {
    UserMealResponseDTO addUserMealFromFood(AddUserMealFromFoodRequestDTO dto, Long goalPeriodId);
    void addUserMealCustom(AddUserMealCustomRequestDTO dto, Long userId, Long goalPeriodId); //저장 후 응답은 없음
    void updateQuantity(Long recordId, Float quantity);
}

