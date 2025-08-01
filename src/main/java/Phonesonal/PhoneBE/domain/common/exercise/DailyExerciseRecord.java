package Phonesonal.PhoneBE.domain.common.exercise;

import Phonesonal.PhoneBE.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExerciseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalCalories = 0; // 총 칼로리 소모량

    @Column
    @Builder.Default
    private Integer anaerobicMinutes = 0; // 무산소 운동 총 시간

    @Column
    @Builder.Default
    private Integer aerobicMinutes = 0; // 유산소 운동 총 시간

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
