package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.web.dto.Food.NutritionData;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RestController
@RequiredArgsConstructor
// Meal 단위 관리
public class MealQueryService {

    private final UserMealRepository userMealRepository;
    private final RecommendMealRepository recommendMealRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    public NutritionSummaryResponse getNutritionSummary(Long userId, Long goalPeriodId, LocalDate date) {
        // GoalPeriod 확인
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid goalPeriodId"));

        Map<MealTime, NutritionData> summary = new HashMap<>();
        for (MealTime time : MealTime.values()) {
            summary.put(time, new NutritionData());

            // UserMeal 조회 (추가 식단)
            List<UserMeal> userMeals = userMealRepository
                    .findByGoalPeriodAndDateAndMealTime(goalPeriod, date, time);
            for (UserMeal meal : userMeals) {
                summary.get(time).add(meal.getFood(), Optional.ofNullable(meal.getQuantity()).orElse(0f));
            }

            // RecommendMeal 조회 (식단 플랜 내 complete)
            List<RecommendMeal> recommendMeals = recommendMealRepository
                    .findByGoalPeriodAndDateAndMealTimeAndComplete(goalPeriod, date, time, CompleteStatus.COMPLETE);
            for (RecommendMeal meal : recommendMeals) {
                summary.get(time).add(meal.getFood(), Optional.ofNullable(meal.getQuantity()).orElse(0f));
            }
        }

        return new NutritionSummaryResponse(date, summary);
    }
}

