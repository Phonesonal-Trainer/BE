package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.web.dto.Food.UpdateCompleteStatusRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.CompleteStatusResponseDTO;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 식단 플랜 관련
public class RecommendMealCommandService {

    private final RecommendMealRepository recommendMealRepository;

    public CompleteStatusResponseDTO updateCompleteStatus(UpdateCompleteStatusRequestDTO request, Long userId) {
        recommendMealRepository.updateCompleteStatusByGoalPeriod(
                request.getGoalPeriodId(),
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

    // 나중에 meal record에 추가해야될수도 있음.
}