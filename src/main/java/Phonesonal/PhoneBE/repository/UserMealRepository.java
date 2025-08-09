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

    //홈화면 오늘 섭취 칼로리를 위한 데이터
    @Query("SELECT um FROM UserMeal um JOIN FETCH um.food WHERE um.user.id = :userId AND um.date = :date")
    List<UserMeal> findWithFoodByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    MealTime mealTime(MealTime mealTime);
}