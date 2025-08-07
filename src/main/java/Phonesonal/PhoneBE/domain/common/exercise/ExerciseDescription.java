package Phonesonal.PhoneBE.domain.common.exercise;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private Integer step; // 운동 단계

    @Column(columnDefinition = "TEXT")
    private String main; // 주요 설명

    @Column(columnDefinition = "TEXT")
    private String sub; // 보조 설명
}