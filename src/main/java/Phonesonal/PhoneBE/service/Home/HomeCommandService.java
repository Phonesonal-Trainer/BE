package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;

import java.time.LocalDate;

public interface HomeCommandService {
    HomeFullResponseDTO getHomeFullResponse(Long userId);
    HomeResultDTO.HomeMainDTO getHomeData(Long userId);
    HomeResultDTO.HomeExerciseDTO getHomeExercise(Long userId);
    HomeResultDTO.HomeMealPlanDTO getHomeMealPlan(Long userId);
    int getBurnedCaloriesOnDate(Long user, LocalDate date);
    double getRecommendedCaloriesByDate(Long userId,LocalDate date);
    double getRecommendedProteinByDate(Long userId,LocalDate date);
    double getRecommendedCarbByDate(Long userId,LocalDate date);
    double getRecommendedFatByDate(Long userId,LocalDate date);
    double getTodayConsumedCaloriesByDate(Long userId,LocalDate date);
    int getTodayCaloriesBurnedByUser(Long userId, LocalDate date);
    String HomeMealPercentageStatus(int percentage);
    String HomeExercisePercentageStatus(int percentage);
    int getTodayAnaerobicExerciseTimeByDate(Long userId);
    int getTodayAerobicExerciseTimeByDate(Long userId);

}
