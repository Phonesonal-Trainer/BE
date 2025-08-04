package Phonesonal.PhoneBE.service.Home;


import Phonesonal.PhoneBE.domain.BodyPhoto;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.repository.BodyPhotoRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.web.dto.Home.BodyPhoto.BodyPhotoRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.BodyPhoto.BodyPhotoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BodyPhotoServiceImpl {

    private final BodyPhotoRepository bodyPhotoRepository;

    public BodyPhoto saveBodyPhotoMetadata(CustomUserDetails userDetails, BodyPhotoRequestDTO request) {
        User user = userDetails.getUser();

        BodyPhoto bodyPhoto = BodyPhoto.builder()
                .fileName(request.getFileName())
                .filePath(request.getFilePath())
                .user(user)
                .build();

        return bodyPhotoRepository.save(bodyPhoto);
    }


    public BodyPhotoResponseDTO getBodyPhotoMetaData(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        BodyPhoto photo = bodyPhotoRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("MetaBodyPhotoData not found"));


        return BodyPhotoResponseDTO.builder()
                .userId(userId)
                .fileName(photo.getFileName())
                .filePath(photo.getFilePath())
                .build();
    }
}
