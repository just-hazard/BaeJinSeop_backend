package wirebarley.task.remittanceservice.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WithdrawalRequest {
    private Long accountId;
    private BigDecimal amount;
}
