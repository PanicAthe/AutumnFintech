package panicathe.autumnfintech.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "계정이 비활성화되었습니다."),
    TRANSFER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "이체 한도를 초과했습니다."),
    TRANSACTION_CANNOT_BE_CANCELLED(HttpStatus.BAD_REQUEST, "송금 거래는 1시간 내에만 취소할 수 있습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    RECEIVER_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "수신자 계좌를 찾을 수 없습니다."),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이메일이 이미 존재합니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "사용자명이 이미 존재합니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "잘못된 인증 정보입니다."),
    USER_HAS_ACTIVE_ACCOUNTS(HttpStatus.BAD_REQUEST, "활성화된 계좌가 있는 사용자는 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

}

