package Phonesonal.PhoneBE.service.Home.InbodyService;

import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface InbodyCommandService {
    Inbody extractInbodyData(CustomUserDetails userDetails, MultipartFile inbodyPicture);
}
