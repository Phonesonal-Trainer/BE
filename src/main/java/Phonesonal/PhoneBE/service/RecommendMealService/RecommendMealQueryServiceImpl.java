package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.web.dto.RecommendMealResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendMealQueryServiceImpl implements RecommendMealQueryService {

    private final RecommendMealRepository recommendMealRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    @Override
    public List<RecommendMealResponseDTO> getMealPlans(Long goalPeriodId, LocalDate date, MealTime mealTime) {
        if (date == null) {
            throw new IllegalArgumentException("date 파라미터는 반드시 필요합니다.");
        }

        // 1. goalPeriodId 기준으로 GoalPeriod 조회
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 GoalPeriod가 없습니다."));

        // 2. 해당 GoalPeriod, 날짜, mealTime을 기준으로 RecommendMeal 조회
        List<RecommendMeal> meals = recommendMealRepository.findByGoalPeriodAndDate(goalPeriod, date, mealTime);

        return meals.stream()
                .map(meal -> {
                    int weekNumber = DateUtil.calculateWeek(goalPeriod.getStartDate(), meal.getDate());
                    return RecommendMealResponseDTO.builder()
                            .foodId(meal.getFood().getFoodId())
                            .foodName(meal.getFood().getName())
                            .mealTime(meal.getMealTime())
                            .date(meal.getDate())
                            .quantity(meal.getQuantity())
                            .complete(meal.getComplete().name())
                            .weekNumber(weekNumber)
                            .build();
                })
                .collect(Collectors.toList());
    }


}