package Phonesonal.PhoneBE.service.Home.InbodyService;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.InbodyImageRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.repository.WeightRecordRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.OpenAiVisionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class InbodyCommandServiceImpl implements InbodyCommandService {

    private final ObjectMapper objectMapper;
    private final GoalPeriodRepository goalPeriodRepository;
    private final UserRepository userRepository;
    private final OpenAiVisionService openAiVisionService;
    private final InbodyPhotoUploadService inbodyPhotoUploadService;
    private final InbodyImageRepository inbodyImageRepository;
    private final WeightRecordRepository weightRecordRepository;

    @Override
    @Transactional
    public Inbody extractInbodyData(CustomUserDetails userDetails, MultipartFile inbodyPicture) {

        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();
        Long userId = userDetails.getUser().getId();

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.INVALID_GOAL_PERIOD));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        try {
        String inbodyPictureUrl = inbodyPhotoUploadService.createInbodyPhotoUpload(inbodyPicture);

        // 2. Openai API 호출 및 구현
        String responseJson = openAiVisionService.analyzeInbodyImage(inbodyPictureUrl);

        // JSON 파싱
        JsonNode root = objectMapper.readTree(responseJson);
        String content = root.get("choices").get(0).get("message").get("content").asText();

        // GPT가 반환한 JSON 문자열을 Inbody 객체로 변환
        Inbody extractedInbody = objectMapper.readValue(content, Inbody.class);


        BigDecimal weight = extractedInbody.getWeight();

        //몸무게 기록테이블에 저장
        WeightRecord weightRecord = WeightRecord.builder()
                .weight(weight)
                .recordDate(LocalDateTime.now())
                .user(user)
                .goalPeriod(goalPeriod)
                .build();

        weightRecordRepository.save(weightRecord);

        // 추가 데이터 세팅
        extractedInbody.setUser(user);
        extractedInbody.setGoalPeriod(goalPeriod);
        extractedInbody.setCreatedAt(LocalDateTime.now());
        extractedInbody.setImageUrl(inbodyPictureUrl);

        // 저장 및 반환
        return inbodyImageRepository.save(extractedInbody);

        } catch (IOException e) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }
    }
}
