package Phonesonal.PhoneBE.domain.mapping;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.enums.exercise.State;
import Phonesonal.PhoneBE.domain.enums.exercise.Weekday;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExercise {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer count; // 유저 설정 운동 횟수

    @Column
    private Integer weight; // 유저 설정 운동 중량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state; // 운동 상태 (예: 진행 중, 완료 등)

    @Column(nullable = false)
    private LocalDate exerciseDate; // 운동 한 날짜

    @Column
    private Integer setCount; // 유저 설정 운동 세트 수

    @Column(nullable = false)
    @Builder.Default
    private Boolean bookmark = false; // 북마크 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise; // 운동 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 커스텀 운동용 추가 필드들
    @Column
    private String customExerciseName; // 실제 운동 이름 (exercise_id=999999일 때 사용)

    @Column
    private Integer caloriesBurned; // 소모 칼로리 (커스텀 운동용)

    @Column
    @Enumerated(EnumType.STRING)
    private CustomExerciseType customExerciseType; // 커스텀 운동 타입

    // 커스텀 운동인지 확인하는 메서드
    public boolean isCustomExercise() {
        return exercise != null && exercise.getId().equals(999999L);
    }

    public enum CustomExerciseType {
        anaerobic, // 무산소
        aerobic,   // 유산소
        etc
    }

    // 목표 기간
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id")
    private GoalPeriod goalPeriod; // 목표 기간 정보, null일 경우 목표 기간 없음
}
