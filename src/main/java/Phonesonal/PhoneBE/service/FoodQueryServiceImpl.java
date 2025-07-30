package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.repository.Food.FoodRepository;
import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodQueryServiceImpl implements FoodQueryService {

    private final FoodRepository foodRepository;

    @Override
    public List<SearchFoodResponseDTO> searchFoods(String keyword) {
        List<Food> foods = foodRepository.findByNameContainingAndIsCustomFalse(keyword);

        return foods.stream().map(food ->
                SearchFoodResponseDTO.builder()
                        .foodId(food.getFoodId())
                        .name(food.getName())
                        .servingSize(food.getServingSize())
                        // .quantity(food.getQuantity())
                        .calorie(food.getCalorie())
                        .carb(food.getCarb())
                        .protein(food.getProtein())
                        .fat(food.getFat())
                        .imageUrl(food.getImageUrl())
                        .build()
        ).collect(Collectors.toList());
    }
}
