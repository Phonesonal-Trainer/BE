package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.FavoriteFood;
import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, Long> {
    // 즐겨찾기는 user당 지속적인 선호를 반영하기 때문에 유저 단위로 관리하도록
    boolean existsByUserIdAndFood_FoodId(Long userId, Long foodId);
    Optional<FavoriteFood> findByUserAndFood(User user, Food food);
    List<FavoriteFood> findAllByUser(User user);
}
