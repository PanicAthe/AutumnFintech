package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
