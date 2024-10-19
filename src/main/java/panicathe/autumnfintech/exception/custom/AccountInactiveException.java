package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class AccountInactiveException extends BusinessException {
    public AccountInactiveException() {
        super(ErrorCode.ACCOUNT_INACTIVE);
    }
}
