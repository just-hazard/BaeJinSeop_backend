package wirebarley.task.remittanceservice.transaction.application;

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
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.transaction.domain.TransactionRepository;
import wirebarley.task.remittanceservice.transaction.domain.TransactionType;
import wirebarley.task.remittanceservice.transaction.dto.DepositRequest;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private Account account;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        account = new Account("1234", new BigDecimal(0));
    }

    @DisplayName("입금")
    @Test
    void deposit() {
        var accountId = 1L;
        var amount = new BigDecimal(10000);

        transaction = Transaction
                .builder()
                .account(account)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        var response = transactionService.deposit(new DepositRequest(accountId, amount));

        assertEquals(transaction.getAmount(), response.getBalance());
        assertEquals(transaction.getType().name(), response.getType());
        assertEquals(transaction.getAccount().getId(), response.getAccountId());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @DisplayName("존재하지 않는 계좌에 입금")
    @Test
    void deposit_accountNotFound() {
        var accountId = 1L;
        var amount = new BigDecimal(10000);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> transactionService.deposit(new DepositRequest(accountId, amount)));

        assertEquals(ErrorMessage.ACCOUNT_NOT_EXISTS, exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}