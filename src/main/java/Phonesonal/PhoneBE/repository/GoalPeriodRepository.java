package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.GoalPeriod;
import Phonesonal.PhoneBE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoalPeriodRepository extends JpaRepository<GoalPeriod, Long> {
    Optional<GoalPeriod> findByUser_Id(Long userId);
}