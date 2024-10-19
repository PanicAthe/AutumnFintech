package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class UserHasActiveAccountsException extends BusinessException {
    public UserHasActiveAccountsException() {
        super(ErrorCode.USER_HAS_ACTIVE_ACCOUNTS);
    }
}
