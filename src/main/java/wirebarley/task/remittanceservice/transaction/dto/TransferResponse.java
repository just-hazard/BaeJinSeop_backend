package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.util.BankUtil;
import wirebarley.task.remittanceservice.util.TimeUtil;

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
                    sendTransaction.getAccount().getName(),
                    sendTransaction.getAccount().getAccountNumber(),
                    BankUtil.removeDecimalPoint(sendTransaction.getAccount().getBalance()),
                    BankUtil.removeDecimalPoint(sendTransaction.getAmount()),
                    BankUtil.removeDecimalPoint(sendTransaction.getFee()),
                    sendTransaction.getType().name(),
                    receiveTransaction.getAccount().getName(),
                    receiveTransaction.getAccount().getAccountNumber(),
                    sendTransaction.getMemo(),
                    TimeUtil.convertToZonedDateTime(sendTransaction.getCreatedDate())
                ),
                new TransferDTO(
                    receiveTransaction.getAccount().getId(),
                    receiveTransaction.getAccount().getName(),
                    receiveTransaction.getAccount().getAccountNumber(),
                    BankUtil.removeDecimalPoint(receiveTransaction.getAccount().getBalance()),
                    BankUtil.removeDecimalPoint(receiveTransaction.getAmount()),
                    null,
                    receiveTransaction.getType().name(),
                    sendTransaction.getAccount().getName(),
                    sendTransaction.getAccount().getAccountNumber(),
                    receiveTransaction.getMemo(),
                    TimeUtil.convertToZonedDateTime(receiveTransaction.getCreatedDate())
                )
        );
    }
}

@AllArgsConstructor
@Getter
class TransferDTO {
    private Long accountId;
    private String name;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal amount;
    private BigDecimal fee;
    private String type;
    private String targetName;
    private String targetAccountNumber;
    private String memo;
    private String date;
}
