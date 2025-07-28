package Phonesonal.PhoneBE.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
public class InfoResponse {
    private String nickName;
    private String email;
    private BigDecimal height;
    private BigDecimal weight;
    private int deadline;
    private long weeksTogether;
}

