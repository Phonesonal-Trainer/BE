package Phonesonal.PhoneBE.domain.common.exercise;

import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import Phonesonal.PhoneBE.domain.mapping.ExerciseBodyPart;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExerciseType type = ExerciseType.etc; // etc 기본값

    @Column
    private String youtubeUrl; // 유튜브 URL

    @Column
    private Integer kcal; // 1회당 칼로리 소모량

    @Column
    private Integer defaultSet; // 기본 세트 수

    @Column(columnDefinition = "TEXT")
    private String description; //운동 설명

    @Column(columnDefinition = "TEXT")
    private String caution; // 운동 주의사항

    @Column
    private String imageUrl; // 운동 이미지 URL

    @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY)
    private List<ExerciseBodyPart> bodyParts; // 운동 부위 정보

    @Column
    @Builder.Default
    private Integer secondsPerRep = 5; // 무산소 운동 시간 (초 단위)
}
