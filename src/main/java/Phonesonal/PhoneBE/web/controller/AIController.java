package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.service.AI.GeminiService;
import Phonesonal.PhoneBE.web.dto.FitnessRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fitness")
@AllArgsConstructor
public class AIController {

    private final GeminiService geminiService;

    @PostMapping("/goals")
    public ResponseEntity<String> generateGoals(@RequestBody FitnessRequest request) {
        String result = geminiService.generateFitnessGoals(
                request.getWeight(),
                request.getHeight()
//                request.getBodyFatPercentage(),
//                request.getMuscleMass()
        );

        return ResponseEntity.ok(result);
    }
}