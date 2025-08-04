package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Food.UserMealCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foods/meal-images")
@Tag(name = "MealImage", description = "식단 이미지 관련 API")
public class MealImageController {
    /*
    private final UserMealCommandService userMealCommandService;

    @Operation(summary = "식단 이미지 업로드")
    @PostMapping(value = "/meal-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadMealImage(
            @RequestPart("image") MultipartFile image,
            @RequestPart("goalPeriodId") Long goalPeriodId,
            @RequestPart("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestPart("mealTime") MealTime mealTime,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        mealImageCommandService.uploadMealImage(image, userId, goalPeriodId, date, mealTime);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "이미지 업로드 완료"));
    }

     */

}


