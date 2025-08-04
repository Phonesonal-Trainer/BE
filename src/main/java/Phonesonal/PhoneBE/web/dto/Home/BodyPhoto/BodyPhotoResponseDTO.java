package Phonesonal.PhoneBE.web.dto.Home.BodyPhoto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BodyPhotoResponseDTO {
    private Long userId;
    private String fileName;
    private String filePath;
}
