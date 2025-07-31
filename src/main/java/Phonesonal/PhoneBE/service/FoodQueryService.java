package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;

import java.util.List;

public interface FoodQueryService {
    List<SearchFoodResponseDTO> searchFoods(String keyword);
}

