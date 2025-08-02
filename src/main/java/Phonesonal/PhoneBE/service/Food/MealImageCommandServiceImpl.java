package Phonesonal.PhoneBE.service.Food;


import Phonesonal.PhoneBE.domain.MealImage;
import Phonesonal.PhoneBE.domain.UserMeal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealImageCommandServiceImpl implements MealImageCommandService {

    /*
    private final MealImageRepository mealImageRepository;
    private final S3Uploader s3Uploader;

    @Override
    public void uploadMealImage(UploadMealImageRequestDTO dto, Long userMealId, Long userId, Long goalPeriodId) {
        UserMeal userMeal = userMealRepository.findById(userMealId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식단 기록이 존재하지 않습니다."));

        if (!userMeal.getUser().getId().equals(userId) || !userMeal.getGoalPeriod().getId().equals(goalPeriodId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 기존 이미지 있으면 덮어쓰기
        MealImage image = mealImageRepository.findByUserMeal(userMeal)
                .orElse(MealImage.builder()
                        .userMeal(userMeal)
                        .build());

        image.setImageUrl(dto.getImageUrl());
        mealImageRepository.save(image);
    }
    */

}
