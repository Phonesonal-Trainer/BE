package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;

import java.time.LocalDate;

public interface HomeCommandService {
    HomeFullResponseDTO getHomeFullResponse(Long userId, Long goalPeriodId);
    HomeResultDTO.HomeMainDTO getHomeData(Long userId, Long goalPeriodId);
    HomeResultDTO.HomeExerciseDTO getHomeExercise(Long userId, Long goalPeriodId);
    HomeResultDTO.HomeMealPlanDTO getHomeMealPlan(Long userId, Long goalPeriodId);
    int getRecommendedBurnedCaloriesOnDate(Long userId,LocalDate date, Long goalPeriodId);
    double getRecommendedCaloriesByDate(Long userId,LocalDate date, Long goalPeriodId);
    double getRecommendedProteinByDate(Long userId,LocalDate date, Long goalPeriodId);
    double getRecommendedCarbByDate(Long userId,LocalDate date, Long goalPeriodId);
    double getRecommendedFatByDate(Long userId,LocalDate date, Long goalPeriodId);
    double getTodayConsumedCaloriesByDate(Long userId,LocalDate date);
    int getTodayCaloriesBurnedByUser(Long userId, LocalDate date);
    String HomeMealPercentageStatus(int percentage);
    String HomeExercisePercentageStatus(int percentage);
    int getTodayAnaerobicExerciseTimeByDate(Long userId);
    int getTodayAerobicExerciseTimeByDate(Long userId);
    String todayComment(LocalDate date);

}
