package Phonesonal.PhoneBE.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal targetWeight; // 목표 몸무게 (kg)

    @Column(nullable = false)
    private BigDecimal targetBMI;

    @Column(name = "muscle_mass")
    private String targetMuscleMass; //목표 골격근량 변동

    @Column(name = "body_fat_rate")
    private BigDecimal targetBodyFatRate; // 목표 체지방률 (%) - 선택사항

    @Column(nullable = false)
    private String recommendedNutrition; //권장 주 영양소 성향

    @Column(nullable = false)
    private int recommendedCalories;   //권장 일일 칼로리

    @Column(nullable = false)
    private int workoutFrequency;   //주간 운동 횟수

    @Column(nullable = false)
    private int cardioDaysPerWeek;  //주간 유산소 일수

    @Column(nullable = false)
    private int cardioMinutesPerWeek; //주간 유산소 시간

    @Column(nullable = false)
    private int strengthTrainingDays;   //주간 근력운동 일수

    @Column(nullable = false)
    private int strengthTrainingTime;   //주간 근력운동 시간

    @Column(nullable = false)
    private String overallRecommendation;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // 사용자 연관
}