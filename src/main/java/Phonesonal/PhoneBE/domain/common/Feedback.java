package Phonesonal.PhoneBE.domain.common;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;
import Phonesonal.PhoneBE.domain.enums.FoodFeedback;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Feedback extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id")
    private GoalPeriod goalPeriod;

    @Enumerated(EnumType.STRING)
    @Column
    private ExerciseFeedback exerciseFeedback;

    @Enumerated(EnumType.STRING)
    @Column
    private FoodFeedback foodFeedback;

    @Column
    private Integer week;

    public void update(ExerciseFeedback exerciseFeedback, FoodFeedback foodFeedback) {
        this.exerciseFeedback = exerciseFeedback;
        this.foodFeedback = foodFeedback;
    }
}
