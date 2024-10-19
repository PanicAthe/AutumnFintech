package panicathe.autumnfintech.exception.custom;

import panicathe.autumnfintech.exception.BusinessException;
import panicathe.autumnfintech.exception.ErrorCode;

public class TransactionNotFoundException extends BusinessException {
    public TransactionNotFoundException() {
        super(ErrorCode.TRANSACTION_NOT_FOUND);
    }
}
