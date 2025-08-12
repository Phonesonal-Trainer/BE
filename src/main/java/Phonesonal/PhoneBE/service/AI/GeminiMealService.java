package Phonesonal.PhoneBE.service.AI;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GeminiMealService {

    /**
     * 7일 식단 플랜 생성 → 파싱 → RecommendMeal 저장.
     *
     * @param user              사용자
     * @param goalPeriod        목표 기간
     * @param startDate         시작 날짜(보통 goalPeriod.getStartDate() 또는 오늘)
     * @param availableFoods    사용 가능한 Food 목록(검색가능/허용된 것만)
     * @param excludeFoodIds    제외할 foodId 목록(알레르기, 기피 식품 등)
     * @param kcalSplitByMeal   끼니별 칼로리 분배 비율(예: B:30, L:40, D:30). null이면 기본값 사용
     * @return 생성된 RecommendMeal ID 목록(필요 시)
     */
    List<Long> generateAndSaveWeeklyMealPlan(
            User user,
            GoalPeriod goalPeriod,
            LocalDateTime startDate,
            List<Food> availableFoods,
            Set<Long> excludeFoodIds,
            Map<MealTime, Integer> kcalSplitByMeal
    );
}
