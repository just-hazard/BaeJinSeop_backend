package wirebarley.task.remittanceservice.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.account.domain.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedDate(),
                account.getModifiedDate()
        );
    }
}
