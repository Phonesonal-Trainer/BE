package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.web.dto.Food.ToggleFavoriteResponseDTO;

public interface FavoriteFoodCommandService {
    ToggleFavoriteResponseDTO toggleFavorite(Long foodId, Long userId);
}
