package wirebarley.task.remittanceservice.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "PK", example = "1")
    private Long accountId;
    @Schema(description = "from: 송금한사람 / to: 송금받는사람", example = "홍길동")
    private String name;
    @Schema(description = "from: 송금한계좌번호 / to: 송금받는계좌번호", example = "1234")
    private String accountNumber;
    @Schema(description = "from: 송금 후 금액 / to: 송금받는 후 금액", example = "10000")
    private BigDecimal balance;
    @Schema(description = "from: 송금 한 금액 / to: 송금받는 금액", example = "10000")
    private BigDecimal amount;
    @Schema(description = "수수료", example = "10000")
    private BigDecimal fee;
    @Schema(description = "송금 타입", example = "DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN")
    private String type;
    @Schema(description = "from: 송금 받은 분 이름 / to: 송금 한 분 이름", example = "홍길동")
    private String targetName;
    @Schema(description = "from: 송금 받은 분 계좌번호 / to: 송금 한 분 계좌번호", example = "1234")
    private String targetAccountNumber;
    @Schema(description = "메모", example = "회비")
    private String memo;
    @Schema(description = "생성 날짜", example = "2025-04-06T00:00:00.513618+09:00")
    private String date;
}
