package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.converter.FeedbackConverter;
import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Feedback.FeedbackCommandService;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackRequestDTO;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report/feedback")
public class FeedbackController {

    private final FeedbackCommandService feedbackCommandService;
    private final GoalPeriodRepository goalPeriodRepository;

    @PostMapping("/post")
    public ApiResponse<FeedbackResponseDTO.CreateResultDTO> create(
            @RequestBody @Valid FeedbackRequestDTO.CreateDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        Feedback feedback = feedbackCommandService.createFeedback(userId, goalPeriodId, request);
        return ApiResponse.onSuccess(FeedbackConverter.toCreateResultDTO(feedback));
    }

    @PatchMapping("/patch")
    public ApiResponse<FeedbackResponseDTO.UpdateResultDTO> update(
            @RequestBody @Valid FeedbackRequestDTO.UpdateDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        Feedback feedback = feedbackCommandService.updateFeedback(userId, request);
        return ApiResponse.onSuccess(FeedbackConverter.toUpdateResultDTO(feedback));
    }

    @GetMapping("/get")
    public ApiResponse<FeedbackResponseDTO.GetResultDTO> getFeedback(
            @RequestParam Integer week,
            @RequestParam Long goalPeriodId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {


        Long userId = userDetails.getUser().getId();
        Feedback feedback = feedbackCommandService.getFeedbackByUserAndWeek(userId, goalPeriodId, week);
        return ApiResponse.onSuccess(FeedbackConverter.toGetResultDTO(feedback));
    }
}
