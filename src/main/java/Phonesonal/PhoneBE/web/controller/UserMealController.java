package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.UserMealCommandService;
import Phonesonal.PhoneBE.web.dto.CompleteStatusResponseDTO;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;
import Phonesonal.PhoneBE.web.dto.Food.UserMealResponseDTO;
import Phonesonal.PhoneBE.web.dto.RecommendMealRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import Phonesonal.PhoneBE.web.dto.Food.AddUserMealFromFoodRequestDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-meals")
public class UserMealController {

    private final UserMealCommandService userMealCommandService;

    @Operation(summary = "유저 식단 직접 추가 (기존 음식 검색 기반)")
    @PostMapping("/from-food")
    public ResponseEntity<ApiResponse<UserMealResponseDTO>> addUserMealFromFood(
            @RequestBody AddUserMealFromFoodRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        UserMealResponseDTO result = userMealCommandService.addUserMealFromFood(requestDTO, userId);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }
}