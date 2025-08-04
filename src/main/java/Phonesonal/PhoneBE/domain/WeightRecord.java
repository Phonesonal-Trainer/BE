package Phonesonal.PhoneBE.domain;


import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저와 ManyToOne 관계, 지연 로딩
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id")
    private GoalPeriod goalPeriod;

    @Column(nullable = false)
    private BigDecimal weight;  // 몸무게

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;  // 기록 날짜(시간까지 포함해야함)

}
