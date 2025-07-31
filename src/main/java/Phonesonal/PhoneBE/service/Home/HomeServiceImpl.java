package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.UserMeal;
import Phonesonal.PhoneBE.repository.DiagnosisRepository;
import Phonesonal.PhoneBE.repository.Food.FoodRepository;
import Phonesonal.PhoneBE.repository.Food.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final FoodRepository foodRepository;


    public double getRecommendedCaloriesByDate(Long userId,LocalDate date) {
        List<RecommendMeal> meals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = meals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getCalorie() != null ? recommendMeal.getFood().getCalorie() : 0)
                .sum();

        return totalCalories;
    }


    public double getTodayConsumedCaloriesByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCalorie() != null ? userMeal.getFood().getCalorie() : 0)
                .sum();

        return totalCalories;
    }

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

    public HomeResultDTO.HomeMainDTO getHomeData(Long userId) {
        User user = userRepository.getReferenceById(userId);
        // 기존 진단이 있는지 확인
        Optional<Diagnosis> existingDiagnosis = diagnosisRepository.findByUserId(userId);
        LocalDate date = LocalDate.now();

        // 임시 값 (프론트에서 홈화면을 테스트할 수 있게)
        double recommendedCalories = getRecommendedCaloriesByDate(userId, date);//추천 섭취 칼로리
        int recommendedBurnedCalories = 1111;//추천 소비 칼로리
        double todayConsumedCalories = getTodayConsumedCaloriesByDate(userId, date);// 오늘 섭취한 칼로리
        int todayBurnedCalories = 1111;//오늘 소비한 칼로리

        // 오늘 먹은 식단의 총 칼로리 - 오늘 운동한 총 소비 칼로리
        double todayCalories = todayConsumedCalories-todayBurnedCalories;
        //추천 식단의 총 칼로리 - 추천 운동의 총 소비 칼로리
        double targetCalories = recommendedCalories-recommendedBurnedCalories;

        int currentWeight = 51;//목표 몸무게 (현재 몸무게는 생성일자 기준으로 가장 빠른 데이터로 출력 추가로 입력 api기능 구현해야함)
        BigDecimal targetWeight = existingDiagnosis.get().getTargetWeight();
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
            //(userId, targetCalories, percentage, status);
    }

    public HomeResultDTO.HomeExerciseDTO getHomeExercise(Long userId) {
        User user = userRepository.getReferenceById(userId);
        //더미 데이터
        String focusedparts = "하체"; // 집중 부위
        int anaerobicExerciseTime = 40; // 무산소 시간
        int aerobicExerciseTime = 15; // 유산소 시간

        return HomeResultDTO.HomeExerciseDTO.builder()
                .anaerobicExerciseTime(anaerobicExerciseTime)
                .aerobicExerciseTime(aerobicExerciseTime)
                .focusedBodyPart(focusedparts)
                .build();
    }

    public HomeResultDTO.HomeMealPlanDTO getHomeMealPlan(Long userId) {
        User user = userRepository.getReferenceById(userId);
        LocalDate date = LocalDate.now();

        double calorie = getTodayConsumedCaloriesByDate(userId, date);
        double carb = getTodayConsumedCarbByDate(userId, date);
        double protein = getTodayConsumedProteinByDate(userId,date);
        double fat = getTodayConsumedFatByDate(userId,date);

        //percentage = mealPercentageCaluator() 식단 퍼센테이지 계산 클래스 구현 필요
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
