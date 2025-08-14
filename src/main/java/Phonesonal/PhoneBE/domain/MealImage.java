package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 기준으로 소유자 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 날짜 및 GoalPeriod
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id", nullable = false)
    private GoalPeriod goalPeriod;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private MealTime mealTime;

    @Column(length = 255, nullable = false)
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
