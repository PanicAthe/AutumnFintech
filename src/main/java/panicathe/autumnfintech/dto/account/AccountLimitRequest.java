package panicathe.autumnfintech.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 계좌 송금 한도 설정을 위한 DTO
@Getter
@Setter
public class AccountLimitRequest {
    @NotNull
    private BigDecimal newLimit;
}
