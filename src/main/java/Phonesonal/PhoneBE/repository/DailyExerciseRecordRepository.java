package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.DailyExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyExerciseRecordRepository extends JpaRepository<DailyExerciseRecord, Long> {
    Optional<DailyExerciseRecord> findByUserAndDate(User user, LocalDate date);
}
