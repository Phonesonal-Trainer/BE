package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
