package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.web.dto.Food.RecommendMealResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface RecommendMealQueryService {
    List<RecommendMealResponseDTO> getMealPlans(Long goalPeriodId, LocalDate date, MealTime mealTime); // goalPeriodId 기준으로 수정
}