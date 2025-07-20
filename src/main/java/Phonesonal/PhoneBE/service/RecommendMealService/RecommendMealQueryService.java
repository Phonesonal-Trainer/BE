package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.web.dto.MealPlanResponse;

import java.time.LocalDate;
import java.util.List;

public interface RecommendMealQueryService {
    List<MealPlanResponse> getMealPlans(Long userId, Integer weekNumber, LocalDate date);
}