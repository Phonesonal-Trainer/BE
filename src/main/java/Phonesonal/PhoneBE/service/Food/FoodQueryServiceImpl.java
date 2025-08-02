package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.repository.FavoriteFoodRepository;
import Phonesonal.PhoneBE.repository.FoodRepository;
import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodQueryServiceImpl implements FoodQueryService {

    private final FoodRepository foodRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;

    @Override
    public List<SearchFoodResponseDTO> searchFoods(String keyword, Long userId) {
        List<Food> foods = foodRepository.findByNameContainingAndIsCustomFalse(keyword);

        return foods.stream().map(food -> {
            boolean isFavorite = favoriteFoodRepository.existsByUserIdAndFoodId(userId, food.getFoodId());

            return SearchFoodResponseDTO.builder()
                    .foodId(food.getFoodId())
                    .name(food.getName())
                    .servingSize(food.getServingSize())
                    .calorie(food.getCalorie())
                    .carb(food.getCarb())
                    .protein(food.getProtein())
                    .fat(food.getFat())
                    .imageUrl(food.getImageUrl())
                    .isFavorite(isFavorite)
                    .build();
        }).collect(Collectors.toList());
    }
}
