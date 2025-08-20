package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;
import Phonesonal.PhoneBE.domain.enums.FoodFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByUserIdAndGoalPeriod_IdAndWeek(Long userId, Long goalPeriodId, Integer week);
    boolean existsByUserIdAndGoalPeriod_IdAndWeek(Long userId, Long goalPeriodId, int week);

    //특정 주차에 특정 운동 피드백을 준 사용자들 조회
    @Query("SELECT DISTINCT f.user.id, f.exerciseFeedback " +
            "FROM Feedback f " +
            "WHERE f.week = :week " +
            "AND f.exerciseFeedback = :exerciseFeedback")
    List<Object[]> findUsersWithExerciseFeedback(
            @Param("week") Integer week,
            @Param("exerciseFeedback") ExerciseFeedback exerciseFeedback);

    // 가장 최근주차 피드백
    Optional<Feedback> findTopByUserIdAndGoalPeriod_IdOrderByWeekDesc(Long userId, Long goalPeriodId);
    // 식단 피드백 준 사용자들 조회
    @Query("""
           SELECT DISTINCT f.user.id, f.foodFeedback
             FROM Feedback f
            WHERE f.week = :week
              AND f.foodFeedback = :foodFeedback
           """)
    List<Object[]> findUsersWithFoodFeedback(
            @Param("week") Integer week,
            @Param("foodFeedback") FoodFeedback foodFeedback
    );

}
