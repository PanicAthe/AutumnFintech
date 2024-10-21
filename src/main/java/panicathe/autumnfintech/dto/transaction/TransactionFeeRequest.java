package panicathe.autumnfintech.dto.transaction;

import java.math.BigDecimal;

// 수수료 설정을 위한 DTO
public class TransactionFeeRequest {
    private BigDecimal newFee;

    public BigDecimal getNewFee() {
        return newFee;
    }

    public void setNewFee(BigDecimal newFee) {
        this.newFee = newFee;
    }
}
