package Phonesonal.PhoneBE.service.Report;

import Phonesonal.PhoneBE.web.dto.Report.ReportResponseDTO;

public interface ReportQueryService {
    ReportResponseDTO.ExerciseFeedbackDTO getWeeklyExerciseReport(Long userId, Long goalPeriodId, int week);
    ReportResponseDTO.MealFeedbackDTO getWeeklyMealReport(Long userId, Long goalPeriodId, int week);
    ReportResponseDTO.WeightFeedbackDTO getWeeklyWeightReport(Long userId, Long goalPeriodId, int week);
    ReportResponseDTO.OverallFeedbackDTO getOverallReport(Long userId, Long goalPeriodId);
}
