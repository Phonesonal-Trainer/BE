package Phonesonal.PhoneBE.web.dto.Feedback;

import lombok.*;
import Phonesonal.PhoneBE.domain.enums.FoodFeedback;
import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;

public class FeedbackResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResultDTO {
        private Long feedbackId;
        private Long userId;
        private ExerciseFeedback exerciseFeedback;
        private FoodFeedback foodFeedback;
    }

    @Getter
    @Builder
    public static class UpdateResultDTO {
        private Long feedbackId;
        private Long userId;
        private ExerciseFeedback exerciseFeedback;
        private FoodFeedback foodFeedback;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetResultDTO {
        private Long feedbackId;
        private Long userId;
        private Integer week;
        private ExerciseFeedback exerciseFeedback;
        private FoodFeedback foodFeedback;
    }
}
