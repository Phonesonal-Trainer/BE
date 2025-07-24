package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercise")
@Tag(name ="UserExercise",description = "유저 운동 관리 API")
public class UserExerciseController {

//    private ExerciseService exerciseService;
//
//    @Operation(summary = "유저 운동 시작")
//    @PatchMapping("/start")
//    public ApiResponse<UserExerciseResponseDTO> startUserExercise(
//            @Valid UserExercise userExercise
//    ) {
//        // 유저 운동 시작 로직
//        UserExercise startedExercise = exerciseService.startUserExercise(userExercise);
//        return ApiResponse.onSuccess(startedExercise);
//    }
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
