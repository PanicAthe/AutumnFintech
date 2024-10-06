package panicathe.autumnfintech.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {
    USER_NOT_FOUND("User not found"),
    ACCOUNT_NOT_FOUND("Account not found"),
    ACCOUNT_INACTIVE("Account is inactive"),
    ACCOUNT_BALANCE_NON_ZERO("Cannot delete account with balance greater than 0"),
    INSUFFICIENT_BALANCE("Insufficient balance"),
    WITHDRAWAL_LIMIT_EXCEEDED("Withdrawal amount exceeds transfer limit"),
    TRANSFER_LIMIT_EXCEEDED("Transfer amount exceeds limit"),
    RECEIVER_ACCOUNT_NOT_FOUND("Receiver account not found"),
    ACCESS_DENIED("Access denied: This account does not belong to the user"),
    TRANSACTION_NOT_FOUND("Transaction not found");

    private final String message;
}


