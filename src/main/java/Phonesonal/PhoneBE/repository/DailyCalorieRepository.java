package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.DailyCalorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCalorieRepository extends JpaRepository<DailyCalorie, Long> {
    Optional<DailyCalorie> findByUserAndDate(User user, LocalDate date);
}
