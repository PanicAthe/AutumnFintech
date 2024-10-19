package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException() {
        super(ErrorCode.INSUFFICIENT_BALANCE);
    }
}
