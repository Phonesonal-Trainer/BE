package Phonesonal.PhoneBE.aws.s3;

import Phonesonal.PhoneBE.config.AmazonConfig;
import Phonesonal.PhoneBE.domain.Uuid;
import Phonesonal.PhoneBE.repository.UuidRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    //식단 사진 업로드 메소드
    public String generateReviewKeyName(Uuid uuid) {
        return amazonConfig.getMealPath() + '/' + uuid.getUuid();
    }

    //인바디 사진 업로드
    public String generateInbodyKeyName(Uuid uuid, Uuid savedUuid) {
        return amazonConfig.getInbodyPath() + '/' + uuid.getUuid();
    }
}
