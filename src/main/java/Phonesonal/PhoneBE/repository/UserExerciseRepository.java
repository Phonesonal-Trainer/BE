package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    // 북마크 여부 확인
    boolean existsByUserIdAndExerciseIdAndBookmarkTrue(Long userId, Long exerciseId);

    // 사용자의 모든 운동 목록 조회
    List<UserExercise> findByUserId(Long userId);

    // 사용자의 북마크된 운동 목록 조회
    List<UserExercise> findByUserIdAndBookmarkTrue(Long userId);

    // 특정 사용자의 특정 운동 조회
    @Query("SELECT ue FROM UserExercise ue WHERE ue.user.id = :userId AND ue.exercise.id = :exerciseId")
    List<UserExercise> findByUserIdAndExerciseId(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);
}
