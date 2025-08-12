package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @SerializedName("weight")
    private BigDecimal weight;

    @SerializedName("muscle_mass")
    private double muscleMass;   // 골격근량

    @SerializedName("body_fat_percentage")
    private double bodyFatMass;  // 체지방량

    // 사용자 기준으로 소유자 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 날짜 및 GoalPeriod
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id")
    private GoalPeriod goalPeriod;

    private LocalDate date;

    @Column(length = 255)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
