package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.util.BankUtil;
import wirebarley.task.remittanceservice.util.TimeUtil;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class TransactionHistoryResponse {
    private List<TransactionHistoryDTO> transactionHistory;

    public static TransactionHistoryResponse from(List<Transaction> transactions) {
        return new TransactionHistoryResponse(
                transactions.stream().map(
                        it -> new TransactionHistoryDTO(
                                it.getAccount().getId(),
                                it.getAccount().getName(),
                                it.getAccount().getAccountNumber(),
                                BankUtil.removeDecimalPoint(it.getAfterAmount()),
                                BankUtil.removeDecimalPoint(it.getAmount()),
                                BankUtil.removeDecimalPoint(it.getFee()),
                                it.getType().name(),
                                it.getTargetName(),
                                it.getTargetAccountNumber(),
                                it.getMemo(),
                                TimeUtil.convertToZonedDateTime(it.getCreatedDate())
                        )
                ).toList());
    };
}

@AllArgsConstructor
@Getter
class TransactionHistoryDTO {
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