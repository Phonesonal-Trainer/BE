package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.MealImageRepository;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.web.dto.Food.NutritionData;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponseDTO;
import Phonesonal.PhoneBE.web.dto.Food.NutritionSummaryResponseDTO.MealSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealQueryService {

    private final UserMealRepository userMealRepository;
    private final RecommendMealRepository recommendMealRepository;
    private final GoalPeriodRepository goalPeriodRepository;
    private final MealImageRepository mealImageRepository; // 최신 1장 조회

    @Transactional(readOnly = true)
    public NutritionSummaryResponseDTO getNutritionSummary(Long userId, Long goalPeriodId, LocalDate date) {
        // 0) GoalPeriod 확인
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        // 1) 식사시간별 버킷 준비
        Map<MealTime, NutritionData> map = new EnumMap<>(MealTime.class);
        for (MealTime time : MealTime.values()) {
            map.put(time, new NutritionData());
        }

        // 2) 각 시간대 집계
        for (MealTime time : MealTime.values()) {
            NutritionData bucket = map.get(time);

            // 2-1) UserMeal (추가 식단) 합산
            List<UserMeal> userMeals = userMealRepository.findByGoalPeriodAndDateAndMealTime(goalPeriod, date, time);
            for (UserMeal meal : userMeals) {
                bucket.add(meal.getFood(), meal.getQuantity());
            }

            // 2-2) RecommendMeal (완료된 것만 actual에 합산)
            List<RecommendMeal> completedRecs = recommendMealRepository
                    .findByGoalPeriodAndDateAndMealTimeAndComplete(goalPeriod, date, time, CompleteStatus.COMPLETE);
            for (RecommendMeal meal : completedRecs) {
                bucket.add(meal.getFood(), meal.getQuantity());
            }

            // 2-3) recordCount (UserMeal 개수 + 완료된 RecommendMeal 개수)
            long recordCount = userMeals.size() + completedRecs.size();
            bucket.setRecordCount(recordCount);

            // 2-4) 최신 이미지
            String imageUrl = mealImageRepository
                    .findTopByUserIdAndGoalPeriodIdAndDateAndMealTimeOrderByCreatedAtDesc(
                            userId, goalPeriod.getId(), date, time
                    )
                    .map(img -> img.getImageUrl())
                    .orElse(null);
            bucket.setImageUrl(imageUrl);

            // 2-5) status
            NutritionData.MealStatus status;
            if (recordCount == 0L) status = NutritionData.MealStatus.NONE;
            else status = (imageUrl == null) ? NutritionData.MealStatus.NO_IMAGE : NutritionData.MealStatus.WITH_IMAGE;
            bucket.setStatus(status);
        }

        // 3) actual 총합 = 각 버킷의 calorie 합(반올림)
        float actualSum = 0f;
        for (MealTime time : MealTime.values()) {
            actualSum += safe(map.get(time).getCalorie());
        }
        int actualTotalCalorie = Math.round(Math.max(actualSum, 0f));

        // 4) planned 총합 = RecommendMeal(complete 여부 무관) 전부 합(동일 로직으로 환산)
        float plannedSum = 0f;
        for (MealTime time : MealTime.values()) {
            List<RecommendMeal> plannedRecs = recommendMealRepository
                    .findByGoalPeriodAndDateAndMealTime(goalPeriod, date, time);
            for (RecommendMeal rm : plannedRecs) {
                plannedSum += calcKcalUsingServingSize(rm.getFood(), rm.getQuantity());
            }
        }
        int plannedTotalCalorie = Math.round(Math.max(plannedSum, 0f));

        // 5) 응답 구성
        NutritionSummaryResponseDTO.MealSummary fixed = new NutritionSummaryResponseDTO.MealSummary(
                map.getOrDefault(MealTime.BREAKFAST, new NutritionData()),
                map.getOrDefault(MealTime.LUNCH, new NutritionData()),
                map.getOrDefault(MealTime.SNACK, new NutritionData()),
                map.getOrDefault(MealTime.DINNER, new NutritionData())
        );

        // 생성자 시그니처 변경 주의
        return new NutritionSummaryResponseDTO(
                date,
                fixed,
                plannedTotalCalorie,
                actualTotalCalorie
        );
    }

    private float calcKcalUsingServingSize(Phonesonal.PhoneBE.domain.Food food, Float actualQuantity) {
        if (food == null) return 0f;

        Float baseQuantity = parseServingSizeToQuantity(food.getServingSize()); // "100g" → 100
        float ratio;
        if (baseQuantity == null || baseQuantity <= 0f)      ratio = 1.0f;
        else if (actualQuantity == null)                     ratio = 1.0f;
        else                                                 ratio = actualQuantity / baseQuantity;

        float baseCal = safe(food.getCalorie());
        return Math.max(baseCal * ratio, 0f);
    }

    private float safe(Float v) {
        return v != null ? v : 0f;
    }

    private Float parseServingSizeToQuantity(String servingSize) {
        if (servingSize == null) return null;
        try {
            return Float.parseFloat(servingSize.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return null;
        }
    }

}
