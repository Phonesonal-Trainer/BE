package Phonesonal.PhoneBE.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class InfoResponse {
    private String nickName;
    private String email;
    private int height;
    private int weight;
    private int deadline;
    private long weeksTogether;
}

