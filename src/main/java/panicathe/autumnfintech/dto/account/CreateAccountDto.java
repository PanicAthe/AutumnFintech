package panicathe.autumnfintech.dto.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CreateAccountDto {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal transferLimit;
}
