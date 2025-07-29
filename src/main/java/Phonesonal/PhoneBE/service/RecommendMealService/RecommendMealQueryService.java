package Phonesonal.PhoneBE.service.RecommendMealService;

import Phonesonal.PhoneBE.web.dto.RecommendMealResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface RecommendMealQueryService {
    List<RecommendMealResponseDTO> getMealPlans(Long userId, LocalDate date);
}