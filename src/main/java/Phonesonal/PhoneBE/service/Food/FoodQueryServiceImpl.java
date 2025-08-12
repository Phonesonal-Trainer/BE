package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.repository.FavoriteFoodRepository;
import Phonesonal.PhoneBE.repository.FoodRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository.UserMealPopularityView;
import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodQueryServiceImpl implements FoodQueryService {

    private final FoodRepository foodRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;
    private final UserMealRepository userMealRepository; // 추가

    @Override
    public List<SearchFoodResponseDTO> searchFoods(String keyword, Long userId, String sort) {

        if ("popular".equalsIgnoreCase(sort)) {
            List<UserMealPopularityView> popular = userMealRepository.findPopularity(keyword);

            Map<Long, Long> usageMap = popular.stream()
                    .collect(Collectors.toMap(
                            UserMealPopularityView::getFoodId,
                            UserMealPopularityView::getUsageCount,
                            (a,b)->a,
                            LinkedHashMap::new));

            Map<Long, Food> foodMap = foodRepository.findAllById(usageMap.keySet()).stream()
                    .collect(Collectors.toMap(Food::getFoodId, f -> f));

            return usageMap.keySet().stream()
                    .map(fid -> {
                        Food f = foodMap.get(fid);
                        boolean isFav = favoriteFoodRepository.existsByUserIdAndFood_FoodId(userId, fid);
                        return toDto(f, isFav);
                    })
                    .collect(Collectors.toList());
        }

        if ("favorite".equalsIgnoreCase(sort)) {
            return favoriteFoodRepository.findFavoriteFoodsByUserOrderByCreatedAtDesc(userId, keyword)
                    .stream()
                    .map(f -> toDto(f, true))
                    .collect(Collectors.toList());
        }

        return foodRepository.findByNameContainingAndIsCustomFalse(keyword).stream()
                .map(f -> toDto(f, favoriteFoodRepository.existsByUserIdAndFood_FoodId(userId, f.getFoodId())))
                .collect(Collectors.toList());
    }

    private SearchFoodResponseDTO toDto(Food food, boolean isFavorite) {
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
    }
}
