package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.web.dto.MealPlanResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface RecommendMealQueryService {
    List<MealPlanResponseDTO> getMealPlans(Long userId, Integer weekNumber, LocalDate date);
}