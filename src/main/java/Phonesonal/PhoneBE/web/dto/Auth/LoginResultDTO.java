package Phonesonal.PhoneBE.web.dto.Auth;

import Phonesonal.PhoneBE.domain.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResultDTO {
    private String accessToken;
    private String refreshToken;
    private String tempToken;
    private User user;
    private boolean isNewUser;
}