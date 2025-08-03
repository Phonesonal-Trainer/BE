package Phonesonal.PhoneBE.web.dto.Home.BodyPhoto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BodyPhotoRequestDTO {
    private String fileName;
    private String filePath;
}
