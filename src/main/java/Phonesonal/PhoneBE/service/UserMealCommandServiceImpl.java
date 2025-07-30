package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.Food.FoodRepository;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.Food.UserMealRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMealCommandServiceImpl implements UserMealCommandService {

    private final UserMealRepository userMealRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    @Override
    public UserMealResponseDTO addUserMealFromFood(AddUserMealFromFoodRequestDTO dto, Long goalPeriodId) {
        Food food = foodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식입니다."));

        GoalPeriod goalPeriod = goalPeriodRepository.findById(dto.getGoalPeriodId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = goalPeriod.getUser();

        UserMeal userMeal = UserMeal.builder()
                .user(user)
                .food(food)
                .goalPeriod(goalPeriod)
                .date(dto.getDate())
                .mealTime(dto.getMealTime())
                .quantity(dto.getQuantity())
                .build();

        UserMeal saved = userMealRepository.save(userMeal);

        return UserMealResponseDTO.builder()
                .recordId(saved.getId())
                .foodName(food.getName())
                .mealTime(saved.getMealTime())
                .date(saved.getDate())
                .quantity(saved.getQuantity())
                .build();
    }
}