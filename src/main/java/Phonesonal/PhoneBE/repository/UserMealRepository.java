package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.UserMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

//홈 api만드는 과정에 필요해서 만들었습니다
public interface UserMealRepository extends JpaRepository<UserMeal, Long> {

    //홈화면 오늘 섭취 칼로리를 위한 데이터
    @Query("SELECT um FROM UserMeal um JOIN FETCH um.food WHERE um.user.id = :userId AND um.date = :date")
    List<UserMeal> findWithFoodByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
