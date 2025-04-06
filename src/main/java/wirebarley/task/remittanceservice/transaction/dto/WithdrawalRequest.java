package wirebarley.task.remittanceservice.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WithdrawalRequest {
    @Schema(description = "출금 금액", example = "100000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
}
