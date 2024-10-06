package panicathe.autumnfintech.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import panicathe.autumnfintech.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@EntityListeners(AuditingEntityListener.class)  // 이 부분을 추가
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_account_id", nullable = false)
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_account_id", nullable = false)
    private Account receiverAccount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal fee;

    @Column(nullable = false)
    private boolean isCancelled;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean canBeCancelled() {
        return this.createdAt.isAfter(LocalDateTime.now().minusHours(1)) && !isCancelled;
    }

    public void cancelTransaction() {
        if (canBeCancelled()) {
            this.isCancelled = true;
            // 추가로 환불 로직 구현
        }
    }
}
