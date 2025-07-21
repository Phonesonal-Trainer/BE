package Phonesonal.PhoneBE.domain.common;

import Phonesonal.PhoneBE.domain.enums.BodyPart;
import Phonesonal.PhoneBE.domain.enums.ExerciseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column
    private Integer defaultCount; // 기본 횟수

    @Column
    private Integer defaultWeight; // 기본 중량

    @Column(nullable = false)
    private ExerciseType type = ExerciseType.etc; // etc 기본값

    @Column
    private BodyPart bodyPart;

    @Column
    private String youtubeUrl; // 유튜브 URL

    @Column
    private Integer kcal; // 1회당 칼로리 소모량

    @Column
    private Integer defaultSetCount; // 기본 세트 수

    @Column
    private String description; //운동 설명

    @Column
    private String caution; // 운동 주의사항
}
