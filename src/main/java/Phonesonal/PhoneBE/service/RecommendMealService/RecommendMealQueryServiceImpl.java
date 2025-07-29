package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
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
    public List<RecommendMealResponseDTO> getMealPlans(Long userId, LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date 파라미터는 반드시 필요합니다.");
        }

        List<RecommendMeal> meals = recommendMealRepository.findByUserIdAndDate(userId, date);

        GoalPeriod goalPeriod = goalPeriodRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 GoalPeriod가 없습니다."));

        return meals.stream()
                .map(meal -> {
                    // 주차 계산
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