package Phonesonal.PhoneBE.domain;

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
public class RecommendMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer weekNumber;

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
            this.complete = CompleteStatus.UNCHECKED;
        }
    } // 기본값 unchecked


}

