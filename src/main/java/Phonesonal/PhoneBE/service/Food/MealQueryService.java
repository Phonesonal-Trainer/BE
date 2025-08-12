package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponseDTO.MealSummary;
import Phonesonal.PhoneBE.web.dto.Food.NutritionData;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
// Meal 단위 관리
public class MealQueryService {

    private final UserMealRepository userMealRepository;
    private final RecommendMealRepository recommendMealRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    public NutritionSummaryResponseDTO getNutritionSummary(Long userId, Long goalPeriodId, LocalDate date) {
        // GoalPeriod 확인
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid goalPeriodId"));

        // 키: Enum으로 고정, 순서: MealTime 선언 순서
        Map<MealTime, NutritionData> map = new EnumMap<>(MealTime.class);

        for (MealTime time : MealTime.values()) {
            map.put(time, new NutritionData());
            // UserMeal 합산
            List<UserMeal> userMeals = userMealRepository
                    .findByGoalPeriodAndDateAndMealTime(goalPeriod, date, time);
            for (UserMeal meal : userMeals) {
                map.get(time).add(meal.getFood(), Optional.ofNullable(meal.getQuantity()).orElse(0f));
            }
            // RecommendMeal 합산
            List<RecommendMeal> recommendMeals = recommendMealRepository
                    .findByGoalPeriodAndDateAndMealTimeAndComplete(goalPeriod, date, time, CompleteStatus.COMPLETE);
            for (RecommendMeal meal : recommendMeals) {
                map.get(time).add(meal.getFood(), Optional.ofNullable(meal.getQuantity()).orElse(0f));
            }
        }

        MealSummary fixed = new MealSummary(
                map.getOrDefault(MealTime.BREAKFAST, new NutritionData()),
                map.getOrDefault(MealTime.LUNCH, new NutritionData()),
                map.getOrDefault(MealTime.SNACK, new NutritionData()),
                map.getOrDefault(MealTime.DINNER, new NutritionData())
        );

        return new NutritionSummaryResponseDTO(date, fixed);
    }
}

