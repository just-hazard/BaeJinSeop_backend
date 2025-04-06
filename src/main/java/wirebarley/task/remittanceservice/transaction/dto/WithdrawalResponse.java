package wirebarley.task.remittanceservice.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.util.BankUtil;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class WithdrawalResponse {
    @Schema(description = "PK", example = "1")
    private Long accountId;
    @Schema(description = "출된 계좌번호", example = "1234")
    private String accountNumber;
    @Schema(description = "출금 후 금액", example = "100000")
    private BigDecimal balance;
    @Schema(description = "출금 금액", example = "100000")
    private BigDecimal amount;
    @Schema(description = "송금 타입", example = "DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN")
    private String type;

    public static WithdrawalResponse from(Transaction transaction) {
        return new WithdrawalResponse(
                transaction.getAccount().getId(),
                transaction.getAccount().getAccountNumber(),
                BankUtil.removeDecimalPoint(transaction.getAccount().getBalance()),
                BankUtil.removeDecimalPoint(transaction.getAmount()),
                transaction.getType().name()
        );
    }
}
