package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserMealRepository extends JpaRepository<UserMeal, Long> {
    // 특정 날짜, 식사 시간 별 조회 (추가 식단)
    List<UserMeal> findByGoalPeriodAndDateAndMealTime(GoalPeriod goalPeriod, LocalDate date, MealTime mealTime);
    List<UserMeal> findByUserIdAndGoalPeriodIdAndDateBetween(
            Long userId, Long goalPeriodId, LocalDate weekStart, LocalDate weekEnd
    );

    // 음식 검색시 빈도순으로 조회
    // um.food 에서 몇 번의 기록이 있는지 음식별로 묶어서 빈도로 취급한다
    // 직접 입력한 음식(custom)은 제외함.
    public interface UserMealPopularityView {
        Long getFoodId();
        Long getUsageCount();
    }

    @Query("""
    select um.food.foodId as foodId, count(um.id) as usageCount
    from UserMeal um
    where um.food.isCustom = false
      and lower(um.food.name) like lower(concat('%', :keyword, '%'))
    group by um.food.foodId
    order by count(um.id) desc
    """)
    List<UserMealPopularityView> findPopularity(@Param("keyword") String keyword);


    //홈화면 오늘 섭취 칼로리를 위한 데이터
    @Query("SELECT um FROM UserMeal um JOIN FETCH um.food WHERE um.user.id = :userId AND um.date = :date")
    List<UserMeal> findWithFoodByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    MealTime mealTime(MealTime mealTime);
}