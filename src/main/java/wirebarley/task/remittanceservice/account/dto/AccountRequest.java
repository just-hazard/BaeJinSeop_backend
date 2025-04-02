package wirebarley.task.remittanceservice.account.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wirebarley.task.remittanceservice.account.domain.Account;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountRequest {
    private String accountNumber;
    private BigDecimal balance;

    public Account toAccount() {
        return new Account(accountNumber, balance);
    }
}
