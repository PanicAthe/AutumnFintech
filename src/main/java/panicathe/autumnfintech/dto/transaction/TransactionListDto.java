package panicathe.autumnfintech.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import panicathe.autumnfintech.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 거래 목록 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListDto {
    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}

