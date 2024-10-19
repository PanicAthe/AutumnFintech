package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
