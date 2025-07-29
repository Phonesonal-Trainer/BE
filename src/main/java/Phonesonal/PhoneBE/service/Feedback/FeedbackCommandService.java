package Phonesonal.PhoneBE.service.Feedback;

import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.web.dto.Feedback.FeedbackRequestDTO;

public interface FeedbackCommandService {

    Feedback createFeedback(Long userId, Long goalPeriodId, FeedbackRequestDTO.CreateDTO dto);

    Feedback updateFeedback(Long userId, FeedbackRequestDTO.UpdateDTO dto);

    Feedback getFeedbackByUserAndWeek(Long userId, Long goalPeriodId, Integer week);
}
