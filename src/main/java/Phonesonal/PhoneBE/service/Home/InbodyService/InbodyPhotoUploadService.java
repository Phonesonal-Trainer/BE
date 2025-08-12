package Phonesonal.PhoneBE.service.Home.InbodyService;

import Phonesonal.PhoneBE.aws.s3.AmazonS3Manager;
import Phonesonal.PhoneBE.domain.Uuid;
import Phonesonal.PhoneBE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class InbodyPhotoUploadService {

    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    public String createInbodyPhotoUpload(MultipartFile InbodyPicture) {

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        /*
        로직 개요 : UUID → DB 저장 (식별용) → S3 key 생성 → S3에 업로드 → 업로드된 파일 URL 반환 → DB에 저장
        사진 url 생성
        s3Manager.generateReviewKeyName(savedUuid) : S3에 저장할 때 사용할 'key' (경로 + 파일명) 생성
        s3Manager.uploadFile(keyName, bodyPicture) : S3에 파일 업로드 후 업로드된 파일의 URL 반환
        */
        String inbodyPictureUrl = s3Manager.uploadFile(s3Manager.generateInbodyKeyName(savedUuid), InbodyPicture);

        return inbodyPictureUrl;
    }
}
