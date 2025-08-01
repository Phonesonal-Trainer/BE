package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.common.WeeklyStamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeeklyStampRepository extends JpaRepository<WeeklyStamp, Long> {
    Optional<WeeklyStamp> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);
}
