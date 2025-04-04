package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
@Getter
public class WithdrawalResponse {
    private Long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal amount;
    private String type;

    public static WithdrawalResponse from(Transaction transaction) {
        return new WithdrawalResponse(
                transaction.getAccount().getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAccount().getBalance().setScale(0, RoundingMode.FLOOR),
                transaction.getAmount().setScale(0, RoundingMode.FLOOR),
                transaction.getType().name()
        );
    }
}
