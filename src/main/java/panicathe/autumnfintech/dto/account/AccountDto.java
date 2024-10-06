package panicathe.autumnfintech.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDto {
    private Long accountId;
    private String accountNumber;
    private String ownerUsername;
    private BigDecimal balance;
    private Boolean isActive;
}
