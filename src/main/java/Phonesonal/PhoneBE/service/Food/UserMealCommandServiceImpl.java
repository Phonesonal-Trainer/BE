package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.FoodRepository;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealCustomRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMealCommandServiceImpl implements UserMealCommandService {

    private final UserMealRepository userMealRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    // quantity 초기값 세팅
    private Float parseServingSizeToQuantity(String servingSize) {
        if (servingSize == null) return 100.0f;
        try {
            return Float.parseFloat(servingSize.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 100.0f;
        }
    }

    @Override
    public UserMealResponseDTO addUserMealFromFood(AddUserMealFromFoodRequestDTO dto, Long goalPeriodId) {
        Food food = foodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식입니다."));

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = goalPeriod.getUser();

        //servingSize 기반으로 초기 quantity 설정
        Float quantity = (food.getQuantity() != null)
                ? food.getQuantity()
                : parseServingSizeToQuantity(food.getServingSize());

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

        String displayedServingSize = saved.getQuantity() + "g";

        return UserMealResponseDTO.builder()
                .recordId(saved.getId())
                .foodId(food.getFoodId())
                .foodName(food.getName())
                .imageUrl(food.getImageUrl())
                .calorie(food.getCalorie())
                .carb(food.getCarb())
                .protein(food.getProtein())
                .fat(food.getFat())
                .defaultServingSize(food.getServingSize())
                .displayedServingSize(displayedServingSize)
                .isCustom(food.getIsCustom())
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

    @Transactional
    @Override
    public void updateQuantity(Long recordId, Float quantity) {
        UserMeal userMeal = userMealRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단입니다."));

        // isCustom == true인 경우 수정 불가(검색 리스트에서 추가한 식단만 수정 가능)
        if (Boolean.TRUE.equals(userMeal.getFood().getIsCustom())) {
            throw new IllegalArgumentException("직접 입력한 식단은 수정할 수 없습니다.");
        }

        userMeal.setQuantity(quantity);
    }

    public void deleteUserMeal(Long recordId, Long userId) {
        UserMeal userMeal = userMealRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단 기록입니다."));

        if (!userMeal.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 식단 기록에 대한 삭제 권한이 없습니다.");
        }

        userMealRepository.delete(userMeal);
    }

}