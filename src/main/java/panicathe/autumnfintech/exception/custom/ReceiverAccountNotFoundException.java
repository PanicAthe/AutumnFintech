package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class ReceiverAccountNotFoundException extends BusinessException {
    public ReceiverAccountNotFoundException() {
        super(ErrorCode.RECEIVER_ACCOUNT_NOT_FOUND);
    }
}
