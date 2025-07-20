package Phonesonal.PhoneBE.repository;

import java.time.LocalDate;

public interface RecommendMealRepository extends JpaRepository<RecommendMeal, Long> {
    List<RecommendMeal> findByUserIdAndWeekNumber(Long userId, Integer weekNumber);
    List<RecommendMeal> findByUserIdAndDate(Long userId, LocalDate date);
}

