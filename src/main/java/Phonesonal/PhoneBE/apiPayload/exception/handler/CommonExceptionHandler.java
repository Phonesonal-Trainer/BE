package Phonesonal.PhoneBE.apiPayload.exception.handler;

import Phonesonal.PhoneBE.apiPayload.code.BaseErrorCode;
import Phonesonal.PhoneBE.apiPayload.exception.GeneralException;

public class CommonExceptionHandler extends GeneralException {

    public CommonExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
