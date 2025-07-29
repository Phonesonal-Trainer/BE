package Phonesonal.PhoneBE.web.dto.Feedback;

import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;
import Phonesonal.PhoneBE.domain.enums.FoodFeedback;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FeedbackRequestDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateDTO {
        private ExerciseFeedback exerciseFeedback;
        private FoodFeedback foodFeedback;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateDTO {
        private Long feedbackId;
        private ExerciseFeedback exerciseFeedback;
        private FoodFeedback foodFeedback;
    }
}
