package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.MealImage;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MealImageRepository extends JpaRepository<MealImage, Long> {

    Optional<MealImage> findTopByUserIdAndGoalPeriodIdAndDateAndMealTimeOrderByCreatedAtDesc(
            Long userId, Long goalPeriodId, LocalDate date, MealTime mealTime
    );

}