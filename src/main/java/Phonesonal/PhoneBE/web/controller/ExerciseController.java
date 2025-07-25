package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.service.ExerciseService;
import Phonesonal.PhoneBE.web.dto.Exercise.ExerciseDetailResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.ExerciseResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Tag(name = "exercise", description = "운동 관련 API")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "모든 운동 목록 조회(검색용)")
    @GetMapping("list")
    public ApiResponse<List<ExerciseResponseDTO>> getExercisesList(
            //@AuthenticationPrincipal CustomerUserDetails userDetails
            @RequestParam Long userId //임시
    ) {
        //Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(exerciseService.getExercisesList(userId));
    }

    @Operation(summary = "운동 상세 조회")
    @GetMapping("/{exerciseId}")
    public ApiResponse<ExerciseDetailResponseDTO> getExerciseDetail(
            @PathVariable Long exerciseId
    ) {
        return ApiResponse.onSuccess(exerciseService.getExerciseDetail(exerciseId));
    }

//    @Operation(summary = "즐겨찾기된 운동 목록 조회")
//    @GetMapping("/bookmarked")
//    public ApiResponse<List<ExerciseResponseDTO>> getBookmarkedExercises(
//            //@AuthenticationPrincipal CustomerUserDetails userDetails
//            @RequestParam Long userId //임시
//    ) {
//        //Long userId = userDetails.getUser().getId();
//        return ApiResponse.onSuccess(exerciseService.getBookmarkedExerciseList(userId));
//    }

}
