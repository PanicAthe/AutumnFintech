package panicathe.autumnfintech.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TranactionDto {

    @NotBlank
    private String receiverAccountNumber;

    @NotNull
    private BigDecimal amount;
}
