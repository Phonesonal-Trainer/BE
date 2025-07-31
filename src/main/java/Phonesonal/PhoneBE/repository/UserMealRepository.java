package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserMealRepository extends JpaRepository<UserMeal, Long> {
    List<UserMeal> findByGoalPeriodAndDateAndMealTime(GoalPeriod goalPeriod, LocalDate date, MealTime mealTime);
}