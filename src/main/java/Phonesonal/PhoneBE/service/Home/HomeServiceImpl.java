package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeCommandService {

    private final UserRepository userRepository;

    public HomeResultDTO.HomeMainDTO getHomeData(Long userId) {
        User user = userRepository.getReferenceById(userId);

        // 임시 값 (프론트에서 홈화면을 테스트할 수 있게)
        int consumedCalories = 1900;// 오늘 먹은 식단의 총 칼로리 - 오늘 운동한 총 소비 칼로리
        int targetCalories = 2000;//추천 식단의 총 칼로리 - 추천 운동의 총 소비 칼로리
        int currentWeight = 51;//목표 몸무게 (현재 몸무게는 생성일자 기준으로 가장 빠른 데이터로 출력 추가로 입력 api기능 구현해야함)
        int targetWeight = 50;
        int percentage = (int) (((double) consumedCalories / targetCalories) * 100);
        String comment = "테스트 코멘트 입니다.";
        LocalDate date = LocalDate.now();

        String status;
        if (percentage < 90) {
            status = "부족";
        } else if (percentage < 110) {
            status = "적정";
        } else {
            status = "초과";
        }

        return HomeResultDTO.HomeMainDTO.builder()
                .userId(userId)
                .targetCalories(targetCalories)
                .percentage(percentage)
                .status(status)
                .date(date)
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

        //더미 데이터
        Float calorie = 111f;
        Float carb = 111f;
        Float protein = 111f;
        Float fat = 111f;
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
