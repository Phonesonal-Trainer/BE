package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 식단플랜 (주차 간 추천한 식단)
public class RecommendMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private MealTime mealTime;

    private LocalDate date;

    private Float quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private CompleteStatus complete;

    @PrePersist
    public void prePersist() {
        if (this.complete == null) {
            this.complete = CompleteStatus.INCOMPLETE;
        }
    } // 기본값 incomplete

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id", nullable = false)
    private GoalPeriod goalPeriod;

}
