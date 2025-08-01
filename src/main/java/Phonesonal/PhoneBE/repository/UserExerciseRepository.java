package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    List<UserExercise> findByUserIdAndExerciseDate(Long userId, LocalDate exerciseDate);

    //홈화면 조회시 필요한 추천 운동에 대한 칼로리소비 데이터 끌어오기
    @Query("SELECT ue FROM UserExercise ue JOIN FETCH ue.exercise WHERE ue.user.id = :userId AND ue.exerciseDate = :exerciseDate")
    List<UserExercise> findWithExerciseByUserIdAndDate(@Param("userId") Long userId, @Param("exerciseDate") LocalDate exerciseDate);

    /*
    //대강 이런 느낌으로 구현할 것 합치는 것을 서비단에서 사용하지 않은 이유는 데이터가 많아지면 엔티티 전체를 가져오기에 db단에서 처리하는 것이 메모리 사용측면에서 효율 적임
    @Query("SELECT SUM(ue.count * ue.durationPerCount) FROM UserExercise ue WHERE ue.user.id = :userId AND ue.exerciseDate = :date AND ue.CustomExerciseType = :type")
    Integer findTotalDurationByUserAndDateAndType(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("type") ExerciseType type);
*/
}