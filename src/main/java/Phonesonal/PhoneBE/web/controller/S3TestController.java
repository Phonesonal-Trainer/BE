/*

미사용으로 주석처리

package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.aws.s3.AmazonS3Manager;
import Phonesonal.PhoneBE.domain.MealImage;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Home.photoUploadTestService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/S3tset")
@RequiredArgsConstructor
public class S3TestController {
*/
/*

    private final AmazonS3Manager amazonS3Manager;

    @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadTest(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                 @RequestParam("file") @RequestPart("testPicture") MultipartFile file) {
        String key = "test/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = amazonS3Manager.uploadFile(key, file);
        return ResponseEntity.ok(fileUrl);
    }
*//*


    private final photoUploadTestService photoUploadTestService;

    @PostMapping(value = "/meal-images",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MealImage> uploadTest(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestPart("picture") MultipartFile picture) { //여기서 @RequestPart는 프론트앤드에서 필요한값 프론트와 연동시 맞춰줘야함
        MealImage mealImage = photoUploadTestService.createPhoto(userDetails,picture);
        return ResponseEntity.ok(mealImage);
    }

}
*/
