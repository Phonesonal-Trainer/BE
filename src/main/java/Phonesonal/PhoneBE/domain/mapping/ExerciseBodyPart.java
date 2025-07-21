package Phonesonal.PhoneBE.domain.mapping;

import Phonesonal.PhoneBE.domain.common.BodyPart;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseBodyPart {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private UserExercise userExercise; // 사용자 운동 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_part_id", nullable = false)
    private BodyPart bodyPart; // 운동 부위 정보
}
