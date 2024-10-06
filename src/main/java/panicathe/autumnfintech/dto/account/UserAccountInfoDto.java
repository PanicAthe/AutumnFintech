package panicathe.autumnfintech.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccountInfoDto {
    private String accountNumber;
    private String accountOwner;
}
