package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByUserIdAndGoalPeriod_IdAndWeek(Long userId, Long goalPeriodId, Integer week);
    boolean existsByUserIdAndGoalPeriod_IdAndWeek(Long userId, Long goalPeriodId, int week);


}
