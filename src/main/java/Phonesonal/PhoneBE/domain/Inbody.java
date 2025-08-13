package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inbody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inbodyId;

    @JsonProperty("weight")
    private BigDecimal weight;

    @JsonProperty("muscle_mass")
    private double muscleMass;   // 골격근량

    @JsonProperty("body_fat_percentage")
    private double bodyFatPercentage;  // 체지방량

    // 사용자 기준으로 소유자 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 날짜 및 GoalPeriod
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id")
    private GoalPeriod goalPeriod;

    @Column(length = 255)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
