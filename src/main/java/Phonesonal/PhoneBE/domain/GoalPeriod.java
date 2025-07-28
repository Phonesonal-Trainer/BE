package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GoalPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalPeriodId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public void changeEndDate(LocalDate newEndDate) {
        this.endDate = newEndDate;
    }
}