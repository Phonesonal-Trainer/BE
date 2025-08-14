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
        // GoalPeriod 확인
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        // 식사시간별 DTO 준비
        Map<MealTime, NutritionData> map = new EnumMap<>(MealTime.class);

        for (MealTime time : MealTime.values()) {
            NutritionData bucket = new NutritionData();
            map.put(time, bucket);

            // 1) UserMeal 합산
            List<UserMeal> userMeals = userMealRepository
                    .findByGoalPeriodAndDateAndMealTime(goalPeriod, date, time);

            for (UserMeal meal : userMeals) {
                bucket.add(meal.getFood(), meal.getQuantity());
            }

            // 2) RecommendMeal(completed) 합산
            List<RecommendMeal> recommendMeals = recommendMealRepository
                    .findByGoalPeriodAndDateAndMealTimeAndComplete(
                            goalPeriod, date, time, CompleteStatus.COMPLETE);

            for (RecommendMeal meal : recommendMeals) {
                bucket.add(meal.getFood(), meal.getQuantity());
            }

            // 3) recordCount = UserMeal 수 + RecommendMeal(완료) 수
            long recordCount = userMeals.size() + recommendMeals.size();
            bucket.setRecordCount(recordCount);

            // 4) 최신 이미지 URL 조회
            String imageUrl = mealImageRepository
                    .findTopByUserIdAndGoalPeriodIdAndDateAndMealTimeOrderByCreatedAtDesc(
                            userId, goalPeriod.getId(), date, time
                    )
                    .map(img -> img.getImageUrl())
                    .orElse(null);
            bucket.setImageUrl(imageUrl);

            // 5) status 계산
            NutritionData.MealStatus status;
            if (recordCount == 0L) {
                status = NutritionData.MealStatus.NONE;
            } else {
                status = (imageUrl == null)
                        ? NutritionData.MealStatus.NO_IMAGE
                        : NutritionData.MealStatus.WITH_IMAGE;
            }
            bucket.setStatus(status);
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
