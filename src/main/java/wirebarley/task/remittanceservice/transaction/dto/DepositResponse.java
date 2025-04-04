package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.util.BankUtil;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class DepositResponse {
    private Long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal amount;
    private String type;


    public static DepositResponse from(Transaction transaction) {
        return new DepositResponse(
                transaction.getAccount().getId(),
                transaction.getAccount().getAccountNumber(),
                BankUtil.removeDecimalPoint(transaction.getAccount().getBalance()),
                BankUtil.removeDecimalPoint(transaction.getAmount()),
                transaction.getType().name()
        );
    }
}
