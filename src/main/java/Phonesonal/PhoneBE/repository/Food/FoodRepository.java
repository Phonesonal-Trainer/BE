package Phonesonal.PhoneBE.repository.Food;

import Phonesonal.PhoneBE.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByNameContainingAndIsCustomFalse(String keyword);
}

