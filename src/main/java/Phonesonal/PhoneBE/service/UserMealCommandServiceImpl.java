package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.Food.FoodRepository;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.Food.UserMealRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealCustomRequestDTO;
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

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = goalPeriod.getUser();

        Float quantity = (food.getQuantity() != null) ? food.getQuantity() : 100.0f;

        int weekNumber = DateUtil.calculateWeek(goalPeriod.getStartDate(), dto.getDate());

        UserMeal userMeal = UserMeal.builder()
                .user(user)
                .food(food)
                .goalPeriod(goalPeriod)
                .date(dto.getDate())
                .mealTime(dto.getMealTime())
                .quantity(quantity)
                .weekNumber(weekNumber)
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


    public void addUserMealCustom(AddUserMealCustomRequestDTO dto, Long userId, Long goalPeriodId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        // 직접 입력한 음식 정보를 Food로 저장
        Food customFood = Food.builder()
                .name(dto.getName())
                .calorie(dto.getCalorie())
                .carb(dto.getCarb())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .createdBy(user)
                .isCustom(true)
                .build();
        foodRepository.save(customFood);

        // weekNumber 계산
        int weekNumber = DateUtil.calculateWeek(goalPeriod.getStartDate(), dto.getDate());

        // UserMeal에 연결
        UserMeal userMeal = UserMeal.builder()
                .user(user)
                .goalPeriod(goalPeriod)
                .food(customFood)
                .quantity(null)  // 직접 입력한 양
                .date(dto.getDate())
                .mealTime(dto.getMealTime())
                .weekNumber(weekNumber)
                .build();

        userMealRepository.save(userMeal);
    }

}