package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class TransferLimitExceededException extends BusinessException {
    public TransferLimitExceededException() {
        super(ErrorCode.TRANSFER_LIMIT_EXCEEDED);
    }
}
