package Phonesonal.PhoneBE.web.dto.Report;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

public class ReportResponseDTO {

    @Getter
    @Builder
    public static class MealFeedbackDTO {
        private Integer week;
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Boolean feedbackExist;
        private Number totalConsumedCalories;
        private Number totalTargetCalories;
        private Number averageDailyCalories;
        private Map<DayOfWeek, Number> dailyCalories;
        private Map<DayOfWeek, Boolean> dailyStamps;
        private String stampMessage;
    }

    @Getter
    @Builder
    public static class WeightFeedbackDTO {
        private Integer week;
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Boolean feedbackExist;
        private Map<DayOfWeek, Number> dailyWeight;

        private String changeFromTargetWeight;  // 예: "+1.4kg"
        private String changeFromInitialWeight; // 예: "-1.2kg"
    }

    @Getter
    @Builder
    public static class ExerciseFeedbackDTO {
        private Integer week;
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Boolean feedbackExist;
        private Map<DayOfWeek, Integer> dailyCalories;

        private Number totalConsumedCalories;
        private Number totalTargetCalories;
        private Number averageDailyCalories;
    }

    @Getter
    @Builder
    public static class MetricProgress {
        private Number initial;       // 0주차 값
        private Number current;       // 현재 값
        private boolean achieved;     // 목표 달성 여부
    }

    @Getter
    @Builder
    public static class WeightProgress {
        private String changeFromInitial;
        private Number initial;       // 0주차 값
        private Number average;
        private Number current;       // 현재 값
        private Number target;
    }

    @Getter
    @Builder
    public static class OverallFeedbackDTO {
        private String purpose;
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Integer deadline;

        private WeightProgress weight;
        private MetricProgress bmi;
        private MetricProgress bodyFat;
        private MetricProgress muscleMass;
    }
}
