package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.Food.UserMealRepository;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMealQueryServiceImpl implements UserMealQueryService {

    private final GoalPeriodRepository goalPeriodRepository;
    private final UserMealRepository userMealRepository;

    @Override
    public List<UserMealResponseDTO> getUserMeals(Long goalPeriodId, LocalDate date, MealTime mealTime) {
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        List<UserMeal> userMeals = userMealRepository.findByGoalPeriodAndDateAndMealTime(goalPeriod, date, mealTime);

        return userMeals.stream().map(userMeal -> {
            Food food = userMeal.getFood();
            return UserMealResponseDTO.builder()
                    .recordId(userMeal.getId())
                    .foodId(food.getFoodId())
                    .foodName(food.getName())
                    .isCustom(food.getIsCustom())
                    .mealTime(userMeal.getMealTime())
                    .date(userMeal.getDate())
                    .quantity(userMeal.getQuantity())
                    .calorie(food.getCalorie())
                    .build();
        }).collect(Collectors.toList());
    }
}

