package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.web.dto.Food.MealImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface MealImageCommandService {
    MealImageResponseDTO uploadMealImage(Long userId,
                                         Long goalPeriodId,
                                         MultipartFile file,
                                         LocalDate date,
                                         MealTime mealTime);
}