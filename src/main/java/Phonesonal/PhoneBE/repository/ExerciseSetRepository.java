package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.mapping.ExerciseSet;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByUserExerciseOrderBySetNumber(UserExercise userExercise);
    List<ExerciseSet> findByUserExerciseIdOrderBySetNumber(Long userExerciseId);
}
