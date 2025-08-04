package Phonesonal.PhoneBE.domain.common;

import Phonesonal.PhoneBE.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStamp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate weekStartDate; // 해당 주의 월요일 날짜

    // 각 요일별 스탬프 획득 여부
    @Column(nullable = false)
    @Builder.Default
    private Boolean mondayStamp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean tuesdayStamp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean wednesdayStamp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean thursdayStamp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean fridayStamp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean saturdayStamp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sundayStamp = false;

    // 특정 요일의 스탬프 상태 업데이트
    public void setDayStamp(java.time.DayOfWeek dayOfWeek, boolean earned) {
        switch (dayOfWeek) {
            case MONDAY -> this.mondayStamp = earned;
            case TUESDAY -> this.tuesdayStamp = earned;
            case WEDNESDAY -> this.wednesdayStamp = earned;
            case THURSDAY -> this.thursdayStamp = earned;
            case FRIDAY -> this.fridayStamp = earned;
            case SATURDAY -> this.saturdayStamp = earned;
            case SUNDAY -> this.sundayStamp = earned;
        }
    }

    // 특정 요일의 스탬프 상태 조회
    public boolean getDayStamp(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> this.mondayStamp;
            case TUESDAY -> this.tuesdayStamp;
            case WEDNESDAY -> this.wednesdayStamp;
            case THURSDAY -> this.thursdayStamp;
            case FRIDAY -> this.fridayStamp;
            case SATURDAY -> this.saturdayStamp;
            case SUNDAY -> this.sundayStamp;
        };
    }
}