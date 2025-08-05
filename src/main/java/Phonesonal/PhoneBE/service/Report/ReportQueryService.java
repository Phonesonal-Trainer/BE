package Phonesonal.PhoneBE.service.Report;

import Phonesonal.PhoneBE.web.dto.Report.ReportResponseDTO;

public interface ReportQueryService {
    ReportResponseDTO.ExerciseFeedbackDTO getWeeklyExerciseFeedback(Long userId, Long goalPeriodId, int week);
    ReportResponseDTO.MealFeedbackDTO getWeeklyMealReport(Long userId, Long goalPeriodId, int week);
    ReportResponseDTO.WeightFeedbackDTO getWeeklyWeightFeedback(Long userId, Long goalPeriodId, int week);
}
