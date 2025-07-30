package Phonesonal.PhoneBE.web.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Builder
    @Getter
    @AllArgsConstructor
    public class HomeResultDTO {

        @Builder
        @Getter
        @AllArgsConstructor
        public static class HomeMainDTO{
            private Long userId;
            private int targetCalories;
            private int percentage;
            private int targetWeight;
            private int currentWeight;
            private String status;
            private String comment;
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
            private Float calorie;
            private Float carb;
            private Float protein;
            private Float fat;
        }
    }
