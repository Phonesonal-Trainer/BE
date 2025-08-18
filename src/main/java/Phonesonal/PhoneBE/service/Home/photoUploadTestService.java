/*

미사용으로 주석처리

package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.aws.s3.AmazonS3Manager;
import Phonesonal.PhoneBE.domain.MealImage;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.Uuid;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.*;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class photoUploadTestService {

    private final UuidRepository uuidRepository;
    private final MealImageRepository mealImageRepository;
    private final UserRepository userRepository;
    private final GoalPeriodRepository goalPeriodRepository;
    private final AmazonS3Manager s3Manager;

    public MealImage createPhoto(CustomUserDetails userDetails, MultipartFile bodyPicture) {

        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();
        Long userId = userDetails.getUser().getId();

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        */
/*
        로직 개요 : UUID → DB 저장 (식별용) → S3 key 생성 → S3에 업로드 → 업로드된 파일 URL 반환 → DB에 저장
        사진 url 생성
        s3Manager.generateReviewKeyName(savedUuid) : S3에 저장할 때 사용할 'key' (경로 + 파일명) 생성
        s3Manager.uploadFile(keyName, bodyPicture) : S3에 파일 업로드 후 업로드된 파일의 URL 반환
        *//*

        String pictureUrl = s3Manager.uploadFile(s3Manager.generateReviewKeyName(savedUuid), bodyPicture);

        MealImage photo = MealImage.builder()
                .user(user)
                .goalPeriod(goalPeriod)
                .date(LocalDate.now())
                .imageUrl(pictureUrl)
                .build();

        return mealImageRepository.save(photo);
    }
}
*/
