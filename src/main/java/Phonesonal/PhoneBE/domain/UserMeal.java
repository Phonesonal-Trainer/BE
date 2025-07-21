package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_meal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private float quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "week_number", nullable = false)
    private int weekNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_time", nullable = false)
    private MealTime mealTime;

    @Column(nullable = false)
    private LocalDate date;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}