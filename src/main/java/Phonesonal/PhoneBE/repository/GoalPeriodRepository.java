package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalPeriodRepository extends JpaRepository<GoalPeriod, Long> {

    List<GoalPeriod> findByUserId(Long userId);
    Optional<GoalPeriod> findByIdAndUserId(Long id, Long userId);
}