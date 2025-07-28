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
    @GetMapping("/myExercises")
    public ApiResponse<List<UserExerciseResponseDTO>> getMyExercises(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate exerciseDate

    ) {
        Long userId = userDetails.getUser().getId();
        List<UserExerciseResponseDTO> myExercises = exerciseService.getMyExercisesList(userId, exerciseDate);
        return ApiResponse.onSuccess(myExercises);
    }

    @Operation(summary = "내 운동 생성(DB에 존재하지 않는 운동)")
    @PostMapping("/personal")
    public ApiResponse<UserExerciseResponseDTO> createUserExercise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateUserExerciseRequestDTO userExercise
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO createdExercise = exerciseService.createUserExercise(userExercise, userId);
        return ApiResponse.onSuccess(createdExercise);
    }

    @Operation(summary = "유저 운동 시작")
    @PatchMapping("/start")
    public ApiResponse<UserExerciseResponseDTO> startUserExercise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long userExerciseID
    ) {
        Long userId = userDetails.getUser().getId();
        UserExerciseResponseDTO startedExercise = exerciseService.startUserExercise(userId, userExerciseID);
        return ApiResponse.onSuccess(startedExercise);
    }
//
//    @Operation(summary = "유저 운동 완료")
//    @PatchMapping("/complete")
//    public ApiResponse<UserExerciseResponseDTO> completeUserExercise(
//            @Valid UserExercise userExercise
//    ) {
//        // 유저 운동 완료 로직
//        UserExercise completedExercise = exerciseService.completeUserExercise(userExercise);
//        return ApiResponse.onSuccess(completedExercise);
//    }
//
//    @Operation(summary = "내 운동 목록 조회")
//    @GetMapping("/my-exercises")
//    public ApiResponse<List<MyExerciseResponseDTO>> getMyExercisesList(
//            //@AuthenticationPrincipal CustomerUserDetails userDetails
//            Long userExerciseId
//    ) {
//        //Long userId = userDetails.getUser().getId();
//        return ApiResponse.onSuccess(exerciseService.getMyExercisesList(userExerciseId));
//    }
}
