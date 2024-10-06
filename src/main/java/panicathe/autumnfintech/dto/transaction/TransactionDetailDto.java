package panicathe.autumnfintech.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import panicathe.autumnfintech.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 거래 상세 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailDto {
    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private TransactionType type;
    private LocalDateTime createdAt;
    private boolean isCancelled;
}
