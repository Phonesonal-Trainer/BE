package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.*;
import Phonesonal.PhoneBE.domain.common.exercise.DailyExerciseRecord;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.*;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static Phonesonal.PhoneBE.apiPayload.code.util.DateUtil.calculateWeek;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeCommandService {

    private final UserRepository userRepository;
    private final RecommendMealRepository recommendMealRepository;
    private final UserMealRepository userMealRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final DailyExerciseRecordRepository dailyExerciseRecordRepository;
    private final WeightRecordRepository weightRecordRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final DiagnosisRepository diagnosesRepository;

    @Transactional(readOnly = true)
    public int getBurnedCaloriesOnDate(Long user, LocalDate date) {
        List<UserExercise> exercises = userExerciseRepository.findWithExerciseByUserIdAndDate(user, date);

        return exercises.stream()
                .filter(userExercise -> userExercise.getExercise() != null && !userExercise.getExercise().getId().equals(999999L)) // 커스텀 운동 제외
                .mapToInt(userExercise -> {
                    Exercise exercise = userExercise.getExercise();
                    if (exercise != null && exercise.getKcal() != null
                            && userExercise.getCount() != null && userExercise.getSetCount() != null) {
                        return exercise.getKcal() * userExercise.getCount() * userExercise.getSetCount();
                    }
                    return 0;
                })
                .sum();
    }
    //추천 칼로리
    public double getRecommendedCaloriesByDate(Long userId,LocalDate date) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getCalorie() != null ? recommendMeal.getFood().getCalorie() : 0)
                .sum();

        return totalCalories;
    }

    //추천 단백질 합계
    public double getRecommendedProteinByDate(Long userId,LocalDate date) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getProtein() != null ? recommendMeal.getFood().getProtein() : 0)
                .sum();

        return totalCalories;
    }

    //추천 탄수화물 합계
    public double getRecommendedCarbByDate(Long userId,LocalDate date) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getCarb() != null ? recommendMeal.getFood().getCarb() : 0)
                .sum();

        return totalCalories;
    }
    //추천 지방 합계
    public double getRecommendedFatByDate(Long userId,LocalDate date) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getFat() != null ? recommendMeal.getFood().getFat() : 0)
                .sum();

        return totalCalories;
    }

    //오늘 섭취한 칼로리 총량
    public double getTodayConsumedCaloriesByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCalorie() != null ? userMeal.getFood().getCalorie() : 0)
                .sum();

        return totalCalories;
    }


    /*
    사용자가 오늘 섭취한 탄,단,지 각 총량

    public double getTodayConsumedProteinByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalProtein = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getProtein() != null ? userMeal.getFood().getProtein() : 0)
                .sum();

        return totalProtein;
    }

    public double getTodayConsumedCarbByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCarb = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCarb() != null ? userMeal.getFood().getCarb() : 0)
                .sum();

        return totalCarb;
    }

    public double getTodayConsumedFatByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalFat = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCalorie() != null ? userMeal.getFood().getCalorie() : 0)
                .sum();

        return totalFat;
    }


    */
    //오늘 소비한 총 칼로리
    public int getTodayCaloriesBurnedByUser(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return dailyExerciseRecordRepository.findByUserAndDate(user, date)
                .map(DailyExerciseRecord::getTotalCalories)
                .orElse(0);
    }

    //식단 플랜 퍼센테이지
    public String HomeMealPercentageStatus(int percentage){

        String status;
        if(percentage == 0){
            status = "시작전";
        } else if (percentage < 90 || percentage > 25) {
            status = "부족";
        } else if (percentage > 90|| percentage < 110) {
            status = "적정";
        } else{
            status = "초과";
        }
        return status;
    }

    //운동플랜 퍼센테이지
    public String HomeExercisePercentageStatus(int percentage){

        String status;
        if(percentage == 0){
            status = "시작전";
        } else if (percentage < 100) {
            status = "부족";
        } else {
            status = "달성";
        }
        return status;
    }

    //추천 무산소 시간
    public int getTodayAnaerobicExerciseTimeByDate(Long userId) {
        int totalAnaerbic = 0;
        if(diagnosesRepository.existsByUserId(userId)){
            Optional<Diagnosis> diagnosis = diagnosesRepository.findByUserId(userId);

            totalAnaerbic = (diagnosis.get().getStrengthTrainingTime())/(diagnosis.get().getWorkoutFrequency());
            return totalAnaerbic;
        }
        return totalAnaerbic;
    }

    //추천 유산소 운동 시간
    public int getTodayAerobicExerciseTimeByDate(Long userId){
        int totalAerobicTime = 0;
        if(diagnosesRepository.existsByUserId(userId)){

            Optional<Diagnosis> diagnosis = diagnosesRepository.findByUserId(userId);
            //추천 유산소 시간
            totalAerobicTime = (diagnosis.get().getCardioMinutesPerWeek())/(diagnosis.get().getCardioDaysPerWeek());
            return totalAerobicTime;
        }
        return totalAerobicTime;
    }


    public HomeResultDTO.HomeMainDTO getHomeData(Long userId) {
        User user = userRepository.getReferenceById(userId);
        // 기존 진단이 있는지 확인
        Optional<Diagnosis> existingDiagnosis = diagnosisRepository.findByUserId(userId);
        Optional<WeightRecord> recordedWeight = weightRecordRepository.findById(userId);

        
        LocalDate date = LocalDate.now();

        double recommendedCalories = getRecommendedCaloriesByDate(userId, date);//추천 섭취 칼로리
        int recommendedBurnedCalories = getBurnedCaloriesOnDate(userId, date);//추천 소비 칼로리
        double todayConsumedCalories = getTodayConsumedCaloriesByDate(userId, date);// 오늘 섭취한 칼로리
        int todayBurnedCalories = getTodayCaloriesBurnedByUser(userId, date);//오늘 소비한 칼로리

        // 오늘 먹은 식단의 총 칼로리 - 오늘 운동한 총 소비 칼로리
        double todayCalories = todayConsumedCalories-todayBurnedCalories;
        //추천 식단의 총 칼로리 - 추천 운동의 총 소비 칼로리
        double targetCalories = recommendedCalories-recommendedBurnedCalories;

        //목표 몸무게 (현재 몸무게는 생성일자 기준으로 가장 빠른 데이터로 출력 추가로 입력 api기능 구현해야함)
        BigDecimal currentWeight = recordedWeight
                .map(WeightRecord::getWeight)
                .orElse(BigDecimal.ZERO);
        BigDecimal targetWeight = existingDiagnosis
                .map(Diagnosis::getTargetWeight)
                .orElse(BigDecimal.ZERO); // 값이 없으면 0으로 기본 처리

        int caloriePercentage = (int)(todayConsumedCalories/recommendedCalories);
        int exercisePercentage = (int)(todayBurnedCalories/recommendedCalories);
        String exerciseStatus = HomeExercisePercentageStatus(exercisePercentage);
        String caloriestatus = HomeMealPercentageStatus(caloriePercentage);
        int presentWeek = calculateWeek(user.getCreated_at().toLocalDate(), date);//유저 생성시간 기준으로 구현
        String comment = "테스트 코멘트 입니다.";


        return HomeResultDTO.HomeMainDTO.builder()
                .userId(userId)
                .targetCalories(targetCalories)
                .todayCalories(todayCalories)
                .caloriePercentage(caloriePercentage)
                .exercisePercentage(exercisePercentage)
                .caloriestatus(caloriestatus)
                .exercisestatus(exerciseStatus)
                .date(date)
                .presentWeek(presentWeek)
                .targetWeight(targetWeight)
                .currentWeight(currentWeight)
                .comment(comment)
                .build();
    }

    public HomeResultDTO.HomeExerciseDTO getHomeExercise(Long userId) {
        String focusedBodyPart = "하체"; // 더미 데이터 집중 부위
        int anaerobicExerciseTime =getTodayAnaerobicExerciseTimeByDate(userId); // 무산소 시간
        int aerobicExerciseTime = getTodayAerobicExerciseTimeByDate(userId); // 유산소 시간

        return HomeResultDTO.HomeExerciseDTO.builder()
                .anaerobicExerciseTime(anaerobicExerciseTime)
                .aerobicExerciseTime(aerobicExerciseTime)
                .focusedBodyPart(focusedBodyPart)
                .build();
    }

    public HomeResultDTO.HomeMealPlanDTO getHomeMealPlan(Long userId) {
        LocalDate date = LocalDate.now();

        double calorie = getTodayConsumedCaloriesByDate(userId, date);
        double carb = getRecommendedCarbByDate(userId, date);
        double protein = getRecommendedProteinByDate(userId,date);
        double fat = getRecommendedFatByDate(userId,date);

        return HomeResultDTO.HomeMealPlanDTO.builder()
                .calorie(calorie)
                .carb(carb)
                .fat(fat)
                .protein(protein)
                .build();
    }

    public HomeFullResponseDTO getHomeFullResponse(Long userId) {
        return HomeFullResponseDTO.builder()
                .main(getHomeData(userId))
                .exercise(getHomeExercise(userId))
                .meal(getHomeMealPlan(userId))
                .build();
    }
}
