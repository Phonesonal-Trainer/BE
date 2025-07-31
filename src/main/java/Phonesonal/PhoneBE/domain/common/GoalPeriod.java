package Phonesonal.PhoneBE.domain.common;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;
import Phonesonal.PhoneBE.domain.enums.FoodFeedback;
import Phonesonal.PhoneBE.domain.mapping.ExerciseBodyPart;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GoalPeriod extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_period_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @OneToMany(mappedBy = "goal_period", fetch = FetchType.LAZY)
    private List<UserExercise> userExercises; // 운동 부위 정보
}
