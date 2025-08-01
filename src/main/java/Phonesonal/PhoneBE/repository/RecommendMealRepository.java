package Phonesonal.PhoneBE.repository;
import Phonesonal.PhoneBE.domain.RecommendMeal;

import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecommendMealRepository extends JpaRepository<RecommendMeal, Long> {
    List<RecommendMeal> findByGoalPeriodAndDateAndMealTime(GoalPeriod goalPeriod, LocalDate date, MealTime mealTime);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE RecommendMeal rm SET rm.complete = :complete " +
            "WHERE rm.user.id = :userId AND rm.food.foodId = :foodId " +
            "AND rm.date = :date AND rm.mealTime = :mealTime")
    void updateCompleteStatus(
            @Param("userId") Long userId,
            @Param("goalPeriodId") Long goalPeriodId,
            @Param("foodId") Long foodId,
            @Param("date") LocalDate date,
            @Param("mealTime") MealTime mealTime,
            @Param("complete") CompleteStatus complete
    );

    //홈화면 목표 칼로리를 위한 데이터
    @Query("SELECT rm FROM RecommendMeal rm JOIN FETCH rm.food WHERE rm.user.id = :userId AND rm.date = :date")
    List<RecommendMeal> findWithFoodByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

}