package Phonesonal.PhoneBE.web.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
    @Getter
    @AllArgsConstructor
    public class HomeResultDTO {

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeMainDTO{
            private Long userId;
            private double targetCalories;
            private double todayCalories;
            private int caloriePercentage;
            private int exercisePercentage;
            private BigDecimal targetWeight;
            private BigDecimal currentWeight;
            private String caloriestatus;
            private String exercisestatus;
            private String comment;
            private int presentWeek;
            private LocalDate date;
        }

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeExerciseDTO{
            private int anaerobicExerciseTime;
            private int aerobicExerciseTime;
            private String focusedBodyPart;


        }

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeMealPlanDTO{
            private double calorie;
            private double carb;
            private double protein;
            private double fat;
        }

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeUserWeightDTO{
            private int userWeight;
        }
    }
