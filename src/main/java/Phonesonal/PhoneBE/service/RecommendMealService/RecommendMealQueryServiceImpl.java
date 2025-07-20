package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.web.dto.MealPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendMealQueryServiceImpl implements RecommendMealQueryService {

    private final RecommendMealRepository recommendMealRepository;

    @Override
    public List<MealPlanResponse> getMealPlans(Long userId, Integer weekNumber, LocalDate date) {
        List<RecommendMeal> meals;

        if (weekNumber != null) {
            meals = recommendMealRepository.findByUserIdAndWeekNumber(userId, weekNumber);
        } else if (date != null) {
            meals = recommendMealRepository.findByUserIdAndDate(userId, date);
        } else {
            throw new IllegalArgumentException("weekNumber 또는 date 파라미터 중 하나는 반드시 필요합니다.");
        }

        return meals.stream()
                .map(MealPlanResponse::from)
                .collect(Collectors.toList());
    }
}
