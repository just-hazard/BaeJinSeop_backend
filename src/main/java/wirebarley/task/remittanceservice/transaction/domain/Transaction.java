package wirebarley.task.remittanceservice.transaction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.common.BaseEntity;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private BigDecimal amount;

    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String counterpartyName;

    private String counterpartyAccountNumber;

    private BigDecimal postTransactionAmount;

    private String memo;

    @Builder
    public Transaction(Account account, BigDecimal amount, BigDecimal fee, TransactionType type, String counterpartyName, String counterpartyAccountNumber, BigDecimal postTransactionAmount, String memo) {
        this.account = account;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.counterpartyName = counterpartyName;
        this.counterpartyAccountNumber = counterpartyAccountNumber;
        this.postTransactionAmount = postTransactionAmount;
        this.memo = memo;
    }
}
