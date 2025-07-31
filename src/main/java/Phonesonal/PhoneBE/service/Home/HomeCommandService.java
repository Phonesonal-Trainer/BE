package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;

public interface HomeCommandService {
    HomeResultDTO.HomeMainDTO getHomeData(Long userId);
    HomeResultDTO.HomeExerciseDTO getHomeExercise(Long userId);

}
