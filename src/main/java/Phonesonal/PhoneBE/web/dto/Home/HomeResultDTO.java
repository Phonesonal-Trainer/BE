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
            private BigDecimal targetWeight;
            private String comment;
            private int presentWeek;
            private LocalDate date;
        }

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeExerciseDTO{
            private int todayBurnedCalories;
            private int todayRecommanedBurnedCalories;
            private int anaerobicExerciseTime;
            private int aerobicExerciseTime;
            private int exercisePercentage;
            private String focusedBodyPart;
            private String exerciseStatus;


        }

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeMealPlanDTO{
            private double todayRecommendedCalories;
            private double todayConsumedCalorie;
            private double carb;
            private double protein;
            private double fat;
            private int caloriePercentage;
            private String calorieStatus;
        }
/*
        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeUserWeightDTO{
            private int userWeight;
        }
        */
    }
