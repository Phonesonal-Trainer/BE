package Phonesonal.PhoneBE.apiPayload.code.status;

import Phonesonal.PhoneBE.apiPayload.code.BaseErrorCode;
import Phonesonal.PhoneBE.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관려 에러
    NOT_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4006", "비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4005", "이미 존재하는 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    SMS_SEND_FAIL(HttpStatus.BAD_GATEWAY, "SMS4001", "인증번호를 발송할 수 없습니다"),
    USER_NOT_FOUND_FOR_FIND_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4010", "일치하는 유저 정보가 존재하지 않습니다."),
    USER_NOT_FOUND_FOR_RESET_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4011", "일치하는 유저 정보가 존재하지 않습니다"),

    // 피드백 에러
    FEEDBACK_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEEDBACK4001","피드백이 존재하지 않습니다."),
    FEEDBACK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FEEDBACK4002","이번 주차 피드백이 이미 존재합니다."),

    // 목표 관리 에러
    INVALID_GOAL_PERIOD(HttpStatus.BAD_REQUEST, "GOAL4002","해당 목표 기간이 존재하지 않습니다."),
    INVALID_WEEK(HttpStatus.BAD_REQUEST, "GOAL4003","범위에 해당하지 않는 주차입니다."),

    //운동 관련 에러
    EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE4041", "운동을 찾을 수 없습니다."),
    EXERCISE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "EXERCISE4001", "이미 완료된 운동입니다."),
    EXERCISE_INVALID_STATE(HttpStatus.BAD_REQUEST, "EXERCISE4002", "유효하지 않은 운동 상태입니다."),
    USER_EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE4042", "사용자의 운동 기록을 찾을 수 없습니다."),
    USER_EXERCISE_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "EXERCISE4003", "이미 시작된 운동입니다."),
    USER_EXERCISE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "EXERCISE4004", "이미 완료된 운동입니다."),
    USER_EXERCISE_NOT_STARTED(HttpStatus.BAD_REQUEST, "EXERCISE4005", "운동이 시작되지 않았습니다."),
    BODY_PART_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE4004", "운동 부위를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
