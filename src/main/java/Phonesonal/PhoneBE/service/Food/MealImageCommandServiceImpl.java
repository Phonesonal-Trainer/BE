package Phonesonal.PhoneBE.service.Food;


import Phonesonal.PhoneBE.aws.s3.AmazonS3Manager;
import Phonesonal.PhoneBE.config.AmazonConfig;
import Phonesonal.PhoneBE.domain.MealImage;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.UserMeal;
import org.springframework.transaction.annotation.Transactional;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.MealImageRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Food.MealImageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class MealImageCommandServiceImpl implements MealImageCommandService {

    private final MealImageRepository mealImageRepository;
    private final UserRepository userRepository;
    private final GoalPeriodRepository goalPeriodRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final AmazonConfig amazonConfig;

    @Transactional
    @Override
    public MealImageResponseDTO uploadMealImage(Long userId,
                                                Long goalPeriodId,
                                                MultipartFile file,
                                                LocalDate date,
                                                MealTime mealTime) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어 있습니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("date 파라미터가 필요합니다. (yyyy-MM-dd)");
        }
        if (mealTime == null) {
            throw new IllegalArgumentException("mealTime 파라미터가 필요합니다. (BREAKFAST/LUNCH/DINNER 등)");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 GoalPeriod 입니다. id=" + goalPeriodId));


        String ext = safeExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String yyyy = date.format(DateTimeFormatter.ofPattern("yyyy"));
        String mm = date.format(DateTimeFormatter.ofPattern("MM"));
        String dd = date.format(DateTimeFormatter.ofPattern("dd"));

        String keyName = String.format("%s/%d/%s/%s/%s/%s/%s.%s",
                amazonConfig.getMealPath(),
                user.getId(),
                yyyy, mm, dd,
                mealTime.name().toLowerCase(Locale.ROOT),
                uuid,
                ext
        );

        String imageUrl = amazonS3Manager.uploadFile(keyName, file); // [수정]

        // 엔티티 생성 (setter 방식)
        MealImage entity = new MealImage();
        entity.setUser(user);
        entity.setGoalPeriod(goalPeriod);
        entity.setDate(date);
        entity.setMealTime(mealTime);
        entity.setImageUrl(imageUrl); // [수정] 이제 변수 존재

        MealImage saved = mealImageRepository.save(entity);

        return MealImageResponseDTO.builder()
                .id(saved.getId())
                .date(saved.getDate())
                .mealTime(saved.getMealTime())
                .imageUrl(saved.getImageUrl())
                .build();
    }

    private String safeExtension(String originalFileName) {
        if (originalFileName == null) return "jpg";
        int idx = originalFileName.lastIndexOf('.');
        if (idx < 0 || idx == originalFileName.length() - 1) return "jpg";
        String ext = originalFileName.substring(idx + 1).toLowerCase(Locale.ROOT);
        if (!ext.matches("^[a-z0-9]{1,5}$")) return "jpg";
        return ext;
    }
}