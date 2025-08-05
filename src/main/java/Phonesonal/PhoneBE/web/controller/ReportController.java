package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.converter.FeedbackConverter;
import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Feedback.FeedbackCommandService;
import Phonesonal.PhoneBE.service.Report.ReportQueryService;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackResponseDTO;
import Phonesonal.PhoneBE.web.dto.Report.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportQueryService reportQueryService;

    @GetMapping("/weight")
    public ApiResponse<ReportResponseDTO.WeightFeedbackDTO> getWeeklyWeightFeedback(
            @RequestParam Integer week,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        ReportResponseDTO.WeightFeedbackDTO result = reportQueryService.getWeeklyWeightFeedback(userId, goalPeriodId, week);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/exercise")
    public ApiResponse<ReportResponseDTO.ExerciseFeedbackDTO> getWeeklyExerciseFeedback(
            @RequestParam Integer week,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        ReportResponseDTO.ExerciseFeedbackDTO result = reportQueryService.getWeeklyExerciseFeedback(userId, goalPeriodId, week);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/foods")
    public ApiResponse<ReportResponseDTO.MealFeedbackDTO> getWeeklyMealFeedback(
            @RequestParam Integer week,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        ReportResponseDTO.MealFeedbackDTO result = reportQueryService.getWeeklyMealReport(userId, goalPeriodId, week);
        return ApiResponse.onSuccess(result);
    }

}
