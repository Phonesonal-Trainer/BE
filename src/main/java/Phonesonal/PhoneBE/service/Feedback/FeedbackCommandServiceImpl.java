package Phonesonal.PhoneBE.service.Feedback;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.converter.FeedbackConverter;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.FeedbackRepository;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class FeedbackCommandServiceImpl implements FeedbackCommandService {
    private final FeedbackRepository feedbackRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    @Transactional
    @Override
    public Feedback createFeedback(Long userId, Long goalPeriodId, FeedbackRequestDTO.CreateDTO dto) {
        GoalPeriod goalPeriod = goalPeriodRepository.findByIdAndUserId(goalPeriodId, userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.INVALID_GOAL_PERIOD));

        int currentWeek = DateUtil.calculateWeek(goalPeriod.getStartDate(), LocalDate.now());

        feedbackRepository.findByUserIdAndGoalPeriod_IdAndWeek(userId, goalPeriodId, currentWeek)
                .ifPresent(f -> {
                    throw new CommonExceptionHandler(ErrorStatus.FEEDBACK_ALREADY_EXISTS);
                });

        Feedback feedback = FeedbackConverter.toEntity(dto, goalPeriod.getUser(), goalPeriod, currentWeek);
        return feedbackRepository.save(feedback);
    }

    @Transactional
    @Override
    public Feedback updateFeedback(Long userId, FeedbackRequestDTO.UpdateDTO dto) {
        Feedback feedback = feedbackRepository.findById(dto.getFeedbackId())
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.FEEDBACK_NOT_FOUND));

        feedback.update(dto.getExerciseFeedback(), dto.getFoodFeedback());
        return feedback;
    }

    @Transactional(readOnly = true)
    @Override
    public Feedback getFeedbackByUserAndWeek(Long userId, Long goalPeriodId, Integer week) {
        return feedbackRepository.findByUserIdAndGoalPeriod_IdAndWeek(userId, goalPeriodId, week)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.FEEDBACK_NOT_FOUND));
    }
}
