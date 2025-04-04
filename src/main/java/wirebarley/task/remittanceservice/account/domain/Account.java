package wirebarley.task.remittanceservice.account.domain;

import jakarta.persistence.*;
import lombok.*;
import wirebarley.task.remittanceservice.common.BaseEntity;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private BigDecimal balance;

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public static Account of(String accountNumber, BigDecimal balance) {
        return new Account(accountNumber, balance);
    }

    public boolean checkAmountExists() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public boolean compareBalance(BigDecimal amount) {
        return balance.compareTo(amount) < 0;
    }

    public void withdrawal(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
