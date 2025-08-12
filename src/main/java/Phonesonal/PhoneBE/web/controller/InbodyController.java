package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Home.InbodyService.InbodyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/inbody")
@RequiredArgsConstructor
public class InbodyController {

    private final InbodyServiceImpl inbodyService;

    @PostMapping("/inbody_extract")
    public ResponseEntity<Inbody> extractInbody(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file") MultipartFile file) throws Exception {
        Inbody result = inbodyService.extractInbodyData(userDetails,file);
        return ResponseEntity.ok(result);
    }
}