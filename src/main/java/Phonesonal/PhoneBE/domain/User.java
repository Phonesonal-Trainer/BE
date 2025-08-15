package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.common.exercise.DailyExerciseRecord;
import Phonesonal.PhoneBE.domain.enums.Gender;
import Phonesonal.PhoneBE.domain.enums.Purpose;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //소셜 이메일
    @Column(nullable = false, unique = true)
    private String email;

    //소셜로부터 받아온 이름
    private String name;

    //직접 사용자가 설정한 이름
    private String nickname;

    //소셜타입(카카오, 애플, 구글)
    private SocialType socialType;

    //성별
    @Enumerated(EnumType.STRING)
    private Gender gender;

    //키
    private BigDecimal height;

    //무게
    private BigDecimal weight;

    //체지방률 (%)
    private BigDecimal bodyFatRate;

    //골격근량 (kg)
    private BigDecimal muscleMass;

    //나이
    private int age;

    //목표기간
    private int deadline;

    //프로필이미지url
    private String profileImageUrl;

    //사용목적
    @Enumerated(EnumType.STRING)
    private Purpose purpose;

    //계정 생성 시간
    private LocalDateTime created_at;

    //목표기간
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id")
    @JsonIgnore
    private GoalPeriod goalPeriod;

    // Diagnosis와의 1:1 관계
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Diagnosis diagnosis;

    // 유저의 일일 칼로리 정보
    @ManyToOne
    @JoinColumn(name = "daily_calorie_id")
    private DailyExerciseRecord dailyExerciseRecord;

}
