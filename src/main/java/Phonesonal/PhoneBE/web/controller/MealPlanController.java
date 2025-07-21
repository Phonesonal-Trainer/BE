package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.service.RecommendMealService.RecommendMealQueryService;
import Phonesonal.PhoneBE.web.dto.RecommendMealResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class MealPlanController {

    private final RecommendMealQueryService recommendMealQueryService;

    @GetMapping("/plans")
    public ResponseEntity<List<RecommendMealResponseDTO>> getMealPlans(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer weekNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<RecommendMealResponseDTO> plans = recommendMealQueryService.getMealPlans(userId, weekNumber, date);
        return ResponseEntity.ok(plans);
    }
}