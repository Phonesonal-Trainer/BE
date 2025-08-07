package Phonesonal.PhoneBE.domain.mapping;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_exercise_id")
    private UserExercise userExercise;

    @Column(nullable = false)
    private Integer setNumber; // 1, 2, 3, 4, 5...

    @Column
    private Integer weight; // 세트별 무게

    @Column
    private Integer reps; // 세트별 횟수

    @Column
    @Builder.Default
    private Boolean completed = false; // 세트 완료 여부
}
