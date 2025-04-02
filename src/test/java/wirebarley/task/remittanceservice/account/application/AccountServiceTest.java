package wirebarley.task.remittanceservice.account.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);
    }

    @Test
    void createAccount() {
        var 계좌번호 = "1234";
        var 금액 = new BigDecimal(10000);
        var mockAccount = new Account(계좌번호, 금액);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        var response = accountService.createAccount(new AccountRequest(계좌번호, 금액));

        assertAll(
            () -> assertThat(response.getAccountNumber()).isEqualTo(계좌번호),
            () -> assertThat(response.getBalance()).isEqualTo(금액)
        );
    }
}