package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.service.ExerciseService;
import Phonesonal.PhoneBE.web.dto.Exercise.request.CreateUserExerciseRequestDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.UserExerciseResponseDTO;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercise")
@Tag(name ="UserExercise",description = "유저 운동 관리 API")
public class UserExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "내 운동 조회")
    @GetMapping("/userExercises")
    public ApiResponse<List<UserExerciseResponseDTO>> getMyExercises(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate exerciseDate

    ) {
        Long userId = userDetails.getUser().getId();
        List<UserExerciseResponseDTO> myExercises = exerciseService.getMyExercisesList(userId, exerciseDate);
        return ApiResponse.onSuccess(myExercises);
    }

    @Operation(summary = "내 운동 생성(DB에 존재하는 운동)")
    @PostMapping("/{ExerciseId}/userExercise")
    public ApiResponse<UserExerciseResponseDTO> createUserExercise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ExerciseId
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO createdExercise = exerciseService.createUserExercise(ExerciseId, userId);
        return ApiResponse.onSuccess(createdExercise);
    }

    @Operation(summary = "내 운동 생성(DB에 존재하지 않는 운동)")
    @PostMapping("/userExercise/custom")
    public ApiResponse<UserExerciseResponseDTO> createUserExercise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateUserExerciseRequestDTO userExercise
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO createdExercise = exerciseService.createCustomUserExercise(userExercise, userId);
        return ApiResponse.onSuccess(createdExercise);
    }

    @Operation(summary = "유저 운동 시작")
    @PatchMapping("userExercises/{userExerciseId}/start")
    public ApiResponse<UserExerciseResponseDTO> startUserExercise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userExerciseId
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO startedExercise = exerciseService.startUserExercise(userId, userExerciseId);
        return ApiResponse.onSuccess(startedExercise);
    }

    @Operation(summary = "유저 운동 완료")
    @PatchMapping("userExercises/{userExerciseId}/complete")
    public ApiResponse<UserExerciseResponseDTO> completeUserExercise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userExerciseId
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO completedExercise = exerciseService.completeUserExercise(userId, userExerciseId);
        return ApiResponse.onSuccess(completedExercise);
    }

    @Operation(summary = "세트 완료")
    @PatchMapping("userExercises/{userExerciseId}/sets/{setId}/complete")
    public ApiResponse<UserExerciseResponseDTO> completeSet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userExerciseId,
            @PathVariable Long setId
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO result = exerciseService.completeSet(userId, userExerciseId, setId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "다음 세트 시작 (휴식 후)")
    @PatchMapping("userExercises/{userExerciseId}/next-set")
    public ApiResponse<UserExerciseResponseDTO> startNextSet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userExerciseId
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO result = exerciseService.startNextSet(userId, userExerciseId);
        return ApiResponse.onSuccess(result);
    }
//    @Operation(summary = "운동 세트 수 변경")
//    @PatchMapping("userExercises/{userExerciseId}/setCount")
//    public ApiResponse<UserExerciseResponseDTO> updateSetCount(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @PathVariable Long userExerciseId,
//            @RequestParam int setCount
//    ) {
//        Long userId = userDetails.getUser().getId();
//        UserExerciseResponseDTO updatedExercise = exerciseService.updateSetCount(userId, userExerciseId, setCount);
//        return ApiResponse.onSuccess(updatedExercise);
//    }
//
//    @Operation(summary = "운동 세트 당 횟수 변경")
//    @PatchMapping("userExercises/{userExerciseId}/count")
//    public ApiResponse<UserExerciseResponseDTO> updateCountPerSet(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @PathVariable Long userExerciseId,
//            @RequestParam int countPerSet
//    ) {
//        Long userId = userDetails.getUser().getId();
//        UserExerciseResponseDTO updatedExercise = exerciseService.updateCountPerSet(userId, userExerciseId, countPerSet);
//        return ApiResponse.onSuccess(updatedExercise);
//    }

}
