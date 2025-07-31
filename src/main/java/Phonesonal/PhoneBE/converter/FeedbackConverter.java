package Phonesonal.PhoneBE.converter;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackRequestDTO;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackResponseDTO;

public class FeedbackConverter {
    public static Feedback toEntity(FeedbackRequestDTO.CreateDTO dto, User user, GoalPeriod goalPeriod, Integer week) {
        return Feedback.builder()
                .user(user)
                .goalPeriod(goalPeriod)
                .week(week)
                .exerciseFeedback(dto.getExerciseFeedback())
                .foodFeedback(dto.getFoodFeedback())
                .build();
    }

    public static FeedbackResponseDTO.CreateResultDTO toCreateResultDTO(Feedback feedback) {
        return FeedbackResponseDTO.CreateResultDTO.builder()
                .feedbackId(feedback.getFeedbackId())
                .userId(feedback.getUser().getId())
                .exerciseFeedback(feedback.getExerciseFeedback())
                .foodFeedback(feedback.getFoodFeedback())
                .build();
    }

    public static FeedbackResponseDTO.UpdateResultDTO toUpdateResultDTO(Feedback feedback) {
        return FeedbackResponseDTO.UpdateResultDTO.builder()
                .feedbackId(feedback.getFeedbackId())
                .userId(feedback.getUser().getId())
                .exerciseFeedback(feedback.getExerciseFeedback())
                .foodFeedback(feedback.getFoodFeedback())
                .build();
    }

    public static FeedbackResponseDTO.GetResultDTO toGetResultDTO(Feedback feedback) {
        return FeedbackResponseDTO.GetResultDTO.builder()
                .feedbackId(feedback.getFeedbackId())
                .userId(feedback.getUser().getId())
                .week(feedback.getWeek())
                .exerciseFeedback(feedback.getExerciseFeedback())
                .foodFeedback(feedback.getFoodFeedback())
                .build();
    }
}
