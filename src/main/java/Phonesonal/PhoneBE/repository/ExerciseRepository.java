package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    //특정 ID들을 제외한 운동 목록 조회
    List<Exercise> findByIdNotIn(List<Long> excludeIds);

    // 특정 부위의 운동 중 특정 운동을 제외한 목록 조회
    @Query("SELECT e FROM Exercise e " +
            "JOIN e.bodyParts bp " +
            "WHERE bp.bodyPart.bodyCategory = :bodyCategory " +
            "AND e.id != :excludeExerciseId")
    List<Exercise> findByBodyCategoryExcludingExercise(
            @Param("bodyCategory") String bodyCategory,
            @Param("excludeExerciseId") Long excludeExerciseId);

}
