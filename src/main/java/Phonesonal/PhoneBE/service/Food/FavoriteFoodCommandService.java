package Phonesonal.PhoneBE.service.Food;

public interface FavoriteFoodCommandService {
    void toggleFavorite(Long foodId, Long userId);
}
