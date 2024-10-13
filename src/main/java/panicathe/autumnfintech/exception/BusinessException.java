package panicathe.autumnfintech.exception;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public String getCode() {
        return errorCode.name(); // enum의 이름을 코드로 사용
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}

