package Phonesonal.PhoneBE.service.AI;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.Food;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.web.dto.Food.GenerateMealRequestDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GeminiMealService {
    // 7일 × 4끼
    String generateWeeklyAllMeals(User user,
                                  Diagnosis diagnosis,
                                  List<Food> availableFoods);

    // 생성 + RecommendMeal 저장(grams만 저장)하고 반환
    int generateAndSaveWeeklyAllMeals(User user,
                                      Diagnosis diagnosis,
                                      GenerateMealRequestDTO req);
}
