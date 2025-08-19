package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.BodyPhoto;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.web.dto.Home.BodyPhoto.BodyPhotoRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.BodyPhoto.BodyPhotoResponseDTO;

public interface BodyPhotoCommandService {
    BodyPhoto saveBodyPhotoMetadata(CustomUserDetails userDetails, BodyPhotoRequestDTO request);
    BodyPhotoResponseDTO getBodyPhotoMetaData(CustomUserDetails userDetails);
}
