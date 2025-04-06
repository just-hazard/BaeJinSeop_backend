package wirebarley.task.remittanceservice.account.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wirebarley.task.remittanceservice.account.domain.Account;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountRequest {
    @Schema(description = "계좌 번호", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountNumber;
    @Schema(description = "초기 입금 금액", example = "100000")
    private BigDecimal balance;
    @Schema(description = "계좌 소유자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    public Account toAccount() {
        return new Account(accountNumber, balance, name);
    }
}
