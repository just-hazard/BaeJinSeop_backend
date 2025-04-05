package wirebarley.task.remittanceservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.util.TimeUtil;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String name;
    private String createdDate;

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getName(),
                TimeUtil.convertToZonedDateTime(account.getCreatedDate())
        );
    }
}
