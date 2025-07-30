package Phonesonal.PhoneBE.repository.Food;

import Phonesonal.PhoneBE.domain.UserMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMealRepository extends JpaRepository<UserMeal, Long> {

}