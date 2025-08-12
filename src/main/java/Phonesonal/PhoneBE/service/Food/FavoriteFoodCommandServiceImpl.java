package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.FavoriteFood;
import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.repository.FavoriteFoodRepository;
import Phonesonal.PhoneBE.repository.FoodRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Food.ToggleFavoriteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FavoriteFoodCommandServiceImpl implements FavoriteFoodCommandService {

    private final FavoriteFoodRepository favoriteFoodRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Override
    public ToggleFavoriteResponseDTO toggleFavorite(Long foodId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("음식 없음"));

        boolean nowFavorite = favoriteFoodRepository.findByUserAndFood(user, food)
                .map(fav -> { favoriteFoodRepository.delete(fav); return false; }) // 해제
                .orElseGet(() -> {                                              // 등록
                    FavoriteFood favorite = FavoriteFood.builder()
                            .user(user).food(food).createdAt(LocalDate.now()).build();
                    favoriteFoodRepository.save(favorite);
                    return true;
                });

        return ToggleFavoriteResponseDTO.builder()
                .foodId(foodId)
                .isFavorite(nowFavorite)
                .build();
    }
}

