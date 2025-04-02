package wirebarley.task.remittanceservice.account.application;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.util.exception.BalanceNotEmptyException;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);
    }

    @DisplayName("계좌 생성")
    @Test
    void createAccount() {
        var accountNumber = "1234";
        var balance = new BigDecimal(10000);
        account = new Account(accountNumber, balance);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        var response = accountService.createAccount(new AccountRequest(accountNumber, balance));

        assertAll(
            () -> assertThat(response.getAccountNumber()).isEqualTo(accountNumber),
            () -> assertThat(response.getBalance()).isEqualTo(balance)
        );
    }

    @DisplayName("계좌 삭제")
    @Test
    void deleteAccount() {
        account = new Account("1234", new BigDecimal(0));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).delete(account);
    }

    @DisplayName("계좌가 없을 때")
    @Test
    void deleteAccount_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.deleteAccount(1L);
        });

        assertEquals(ErrorMessage.ACCOUNT_NOT_EXISTS, exception.getMessage());
        verify(accountRepository, never()).delete(account);
    }

    @DisplayName("계좌에 잔액이 존재할 시")
    @Test
    void deleteAccount_BalanceNotEmpty() {
        account = new Account("1234", new BigDecimal(10000));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        var exception = assertThrows(BalanceNotEmptyException.class, () -> {
            accountService.deleteAccount(1L);
        });

        assertEquals(ErrorMessage.BALANCE_NOT_EMPTY, exception.getMessage());
        verify(accountRepository, never()).delete(account);
    }
}