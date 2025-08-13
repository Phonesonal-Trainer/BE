package Phonesonal.PhoneBE.service.Home.InbodyService;

import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.InbodyImageRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.OpenAiVisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class InbodyServiceImpl {

    private final ObjectMapper objectMapper;
    private final GoalPeriodRepository goalPeriodRepository;
    private final UserRepository userRepository;
    private final OpenAiVisionService openAiVisionService;
    private final InbodyPhotoUploadService inbodyPhotoUploadService;
    private final InbodyImageRepository inbodyImageRepository;


    public Inbody extractInbodyData(CustomUserDetails userDetails, MultipartFile inbodyPicture) throws IOException {

        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();
        Long userId = userDetails.getUser().getId();

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String inbodyPictureUrl = inbodyPhotoUploadService.createInbodyPhotoUpload(inbodyPicture);

        // 2. Openai API 호출 및 구현
        String responseJson = openAiVisionService.analyzeInbodyImage(inbodyPictureUrl);

        // JSON 파싱
        JsonNode root = objectMapper.readTree(responseJson);
        String content = root.get("choices").get(0).get("message").get("content").asText();

        // content 값 확인용 로그
        System.out.println("GPT content:\n" + content);

        // GPT가 반환한 JSON 문자열을 Inbody 객체로 변환
        Inbody extractedInbody = objectMapper.readValue(content, Inbody.class);

        // 추가 데이터 세팅
        extractedInbody.setUser(user);
        extractedInbody.setGoalPeriod(goalPeriod);
        extractedInbody.setCreatedAt(LocalDateTime.now());
        extractedInbody.setImageUrl(inbodyPictureUrl);

        // 저장 및 반환
        return inbodyImageRepository.save(extractedInbody);

    }
}
