package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.util.BankUtil;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class TransferResponse {

    private TransferDTO fromAccount;
    private TransferDTO toAccount;

    public static TransferResponse of(Transaction sendTransaction, Transaction receiveTransaction) {
        return new TransferResponse(
                new TransferDTO(
                    sendTransaction.getAccount().getId(),
                    sendTransaction.getAccount().getAccountNumber(),
                    BankUtil.removeDecimalPoint(sendTransaction.getAccount().getBalance()),
                    BankUtil.removeDecimalPoint(sendTransaction.getAmount()),
                    BankUtil.removeDecimalPoint(sendTransaction.getFee()),
                    sendTransaction.getType().name()
                ),
                new TransferDTO(
                    receiveTransaction.getAccount().getId(),
                    receiveTransaction.getAccount().getAccountNumber(),
                    BankUtil.removeDecimalPoint(receiveTransaction.getAccount().getBalance()),
                    BankUtil.removeDecimalPoint(receiveTransaction.getAmount()),
                    null,
                    receiveTransaction.getType().name()
                )
        );
    }
}

@AllArgsConstructor
@Getter
class TransferDTO {
    private Long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal amount;
    private BigDecimal fee;
    private String type;
}
