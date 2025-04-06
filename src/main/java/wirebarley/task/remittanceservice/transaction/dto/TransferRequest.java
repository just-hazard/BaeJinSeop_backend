package wirebarley.task.remittanceservice.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TransferRequest {
    @Schema(description = "이체할 계좌번호", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String toAccountNumber;
    @Schema(description = "이체할 금액", example = "100000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
}
