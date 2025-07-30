package Phonesonal.PhoneBE.repository.Food;

import Phonesonal.PhoneBE.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    // 필요하면 foodName으로 검색 같은 기능도 추가 가능
    // Optional<Food> findByName(String name);
}
