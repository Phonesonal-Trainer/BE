package Phonesonal.PhoneBE.repository;
import Phonesonal.PhoneBE.domain.RecommendMeal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecommendMealRepository extends JpaRepository<RecommendMeal, Long> {
    List<RecommendMeal> findByUserIdAndWeekNumber(Long userId, Integer weekNumber);
    List<RecommendMeal> findByUserIdAndDate(Long userId, LocalDate date);
}

