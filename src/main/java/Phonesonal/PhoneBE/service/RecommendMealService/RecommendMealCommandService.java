package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.web.dto.CompleteStatusResponseDTO;
import Phonesonal.PhoneBE.web.dto.RecommendMealRequestDTO.UpdateCompleteStatusRequestDTO;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecommendMealCommandService {

    private final RecommendMealRepository recommendMealRepository;

    public CompleteStatusResponseDTO updateCompleteStatus(UpdateCompleteStatusRequestDTO request) {
        CompleteStatus complete = CompleteStatus.valueOf(request.getComplete().toUpperCase());

        recommendMealRepository.updateCompleteStatus(
                request.getUserId(),
                request.getFoodId(),
                request.getDate(),
                request.getMealTime(),
                complete
        );

        return CompleteStatusResponseDTO.builder()
                .foodId(request.getFoodId())
                .complete(complete)
                .build();
    }

    // 나중에 meal record에 추가해야될수도 있음.
}