package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.UserMealRepository;
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

    // 초기 설정용 파싱
    private Float parseServingSizeToQuantity(String servingSize) {
        if (servingSize == null) return null;
        try {
            return Float.parseFloat(servingSize.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return null;
        }
    }

    // 비율 계산
    private Float safeMultiply(Float value, Float ratio) {
        if (value == null || ratio == null) return null;
        return value * ratio;
    }


    @Override
    public List<UserMealResponseDTO> getUserMeals(Long goalPeriodId, LocalDate date, MealTime mealTime) {
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        List<UserMeal> userMeals = userMealRepository.findByGoalPeriodAndDateAndMealTime(goalPeriod, date, mealTime);

        return userMeals.stream().map(userMeal -> {
            Food food = userMeal.getFood();
            Float actualQuantity = userMeal.getQuantity();
            Float defaultQuantity = parseServingSizeToQuantity(food.getServingSize());

            Float ratio = (defaultQuantity != null && defaultQuantity > 0) ? (actualQuantity / defaultQuantity) : 1.0f;

            Float adjustedCalorie = safeMultiply(food.getCalorie(), ratio);
            Float adjustedCarb = safeMultiply(food.getCarb(), ratio);
            Float adjustedProtein = safeMultiply(food.getProtein(), ratio);
            Float adjustedFat = safeMultiply(food.getFat(), ratio);

            // 표시용 servingSize 계산: quantity 수정 안했으면 defaultservingSize, 했으면 quantity 값 기반 표현
            String displayedServingSize = actualQuantity + "g";

            return UserMealResponseDTO.builder()
                    .recordId(userMeal.getId())
                    .foodId(food.getFoodId())
                    .foodName(food.getName())
                    .imageUrl(food.getImageUrl())
                    .isCustom(food.getIsCustom())
                    .mealTime(userMeal.getMealTime())
                    .date(userMeal.getDate())
                    .quantity(actualQuantity)
                    .calorie(adjustedCalorie)
                    .carb(adjustedCarb)
                    .protein(adjustedProtein)
                    .fat(adjustedFat)
                    .defaultServingSize(food.getServingSize())         // 기준값
                    .displayedServingSize(displayedServingSize)        // 보여줄 값
                    .build();
        }).collect(Collectors.toList());
    }
}

