package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByNameContainingAndIsCustomFalse(String keyword);

    @Query("select f from Food f where f.isCustom = false")
    List<Food> findByIsCustomFalse();

}

