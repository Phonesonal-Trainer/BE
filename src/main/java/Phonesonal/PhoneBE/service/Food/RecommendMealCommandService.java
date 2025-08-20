package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.web.dto.Food.UpdateCompleteStatusRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.CompleteStatusResponseDTO;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecommendMealCommandService {

    private final RecommendMealRepository recommendMealRepository;

    public CompleteStatusResponseDTO updateCompleteStatus(UpdateCompleteStatusRequestDTO request, Long userId, Long goalPeriodId) {
        recommendMealRepository.updateCompleteStatusByGoalPeriod(
                goalPeriodId,
                request.getFoodId(),
                request.getDate(),
                request.getMealTime(),
                request.getComplete()
        );


        return CompleteStatusResponseDTO.builder()
                .foodId(request.getFoodId())
                .complete(request.getComplete())
                .build();
    }
}