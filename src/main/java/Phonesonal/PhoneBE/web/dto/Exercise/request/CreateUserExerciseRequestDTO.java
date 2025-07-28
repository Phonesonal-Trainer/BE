package Phonesonal.PhoneBE.web.dto.Exercise.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateUserExerciseRequestDTO { //유저가 직접 운동 만들기
    private Integer count;
    private Integer weight;
    private Integer sets;
    private LocalDate date;
}

