package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class TransactionCannotBeCancelledException extends BusinessException {
    public TransactionCannotBeCancelledException() {
        super(ErrorCode.TRANSACTION_CANNOT_BE_CANCELLED);
    }
}
