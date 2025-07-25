package Phonesonal.PhoneBE.domain.mapping;

import Phonesonal.PhoneBE.domain.User;
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

    @Column(nullable = false)
    private State state; // 운동 상태 (예: 진행 중, 완료 등)

    @Column(nullable = false)
    private Integer weekNumber; // 운동 주차

    @Column(nullable = false)
    private LocalDate date; // 운동 한 날짜

    @Column(nullable = false)
    private Weekday weekday; // 운동 요일

    @Column
    private Integer setCount; // 유저 설정 운동 세트 수

    @Column(nullable = false)
    private Boolean bookmark = false; // 북마크 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise; // 운동 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
