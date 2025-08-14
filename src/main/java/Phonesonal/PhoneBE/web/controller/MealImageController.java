package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Food.MealImageCommandService;
import Phonesonal.PhoneBE.web.dto.Food.MealImageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foods/meal-images")
@Tag(name = "MealImage", description = "식단 이미지 관련 API")
public class MealImageController {

    private final MealImageCommandService mealImageCommandService;

    @Operation(summary = "식단 사진 업로드")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MealImageResponseDTO>> uploadMealImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile file,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("mealTime") MealTime mealTime
    ) {
        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();

        MealImageResponseDTO result = mealImageCommandService.uploadMealImage(
                userId,
                goalPeriodId,
                file,
                date,
                mealTime
        );

        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }
}
