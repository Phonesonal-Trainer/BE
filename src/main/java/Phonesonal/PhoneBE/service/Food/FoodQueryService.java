package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;

import java.util.List;

public interface FoodQueryService {
    List<SearchFoodResponseDTO> searchFoods(String keyword, Long userId, String sort);
}

