package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                                it.getAccount().getAccountNumber(),
                                it.getPostTransactionAmount(),
                                it.getAmount(),
                                it.getFee(),
                                it.getType().name(),
                                it.getCounterpartyName(),
                                it.getCounterpartyAccountNumber(),
                                it.getMemo(),
                                it.getCreatedDate()
                        )
                ).toList());
    };
}

@AllArgsConstructor
@Getter
class TransactionHistoryDTO {
    private Long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal amount;
    private BigDecimal fee;
    private String type;
    private String counterpartyName;
    private String counterpartyAccountNumber;
    private String memo;
    private LocalDateTime date;
}