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

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private BigDecimal amount;

    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Builder
    public Transaction(Account account, BigDecimal amount, BigDecimal fee, TransactionType type) {
        this.account = account;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
    }
}
