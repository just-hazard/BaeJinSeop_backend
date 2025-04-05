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
import wirebarley.task.remittanceservice.transaction.dto.TransferRequest;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalRequest;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;
import wirebarley.task.remittanceservice.util.exception.InsufficientBalanceException;
import wirebarley.task.remittanceservice.util.exception.TransferLimitExceededException;
import wirebarley.task.remittanceservice.util.exception.WithdrawalLimitExceededException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

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
        account = new Account("1234", new BigDecimal(0), "홍길동");
    }

    @DisplayName("입금하다")
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

        var response = transactionService.deposit(new DepositRequest(amount), accountId);

        assertEquals(transaction.getAmount(), response.getBalance());
        assertEquals(transaction.getType().name(), response.getType());
        assertEquals(transaction.getAccount().getId(), response.getAccountId());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @DisplayName("존재하지 않는 계좌에 입금")
    @Test
    void deposit_AccountNotFound() {
        var accountId = 1L;
        var amount = new BigDecimal(10000);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.deposit(new DepositRequest(amount), accountId));

        assertEquals(ErrorMessage.ACCOUNT_NOT_EXISTS, exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("출금하다")
    @Test
    void withdrawal() {
        account = new Account("1234", new BigDecimal(1100000), "홍길동");

        var accountId = 1L;
        var amount = new BigDecimal(500000);

        transaction = Transaction
                .builder()
                .account(account)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()))
                .thenReturn(Optional.of(amount));

        var response = transactionService.withdrawal(new WithdrawalRequest(amount), accountId);

        assertEquals(new BigDecimal(600000), response.getBalance());
        assertEquals(amount, response.getAmount());
        assertEquals(TransactionType.WITHDRAWAL.name(), response.getType());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, times(1)).sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @DisplayName("존재하지 않는 계좌에서 출금 요청 시")
    @Test
    void withdrawal_AccountNotFound() {
        var accountId = 1L;
        var amount = new BigDecimal(900000);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.withdrawal(new WithdrawalRequest(amount), accountId));

        assertEquals(ErrorMessage.ACCOUNT_NOT_EXISTS, exception.getMessage());
        verify(accountRepository, times(1)).findById(any());
        verify(transactionRepository, never()).sumWithdrawalsForToday(any(), any(), any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("출금 시 잔액보다 많을 경우")
    @Test
    void withdrawal_InsufficientBalance() {
        account = new Account("1234", new BigDecimal(800000), "홍길동");

        var accountId = 1L;
        var amount = new BigDecimal(900000);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        var exception = assertThrows(InsufficientBalanceException.class, () ->
                transactionService.withdrawal(new WithdrawalRequest(amount), accountId));

        assertEquals(ErrorMessage.INSUFFICIENT_BALANCE, exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, never()).sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("당일 출금 한도 초과")
    @Test
    void withdrawal_LimitExceeded() {
        account = new Account("1234", new BigDecimal(800000), "홍길동");

        var accountId = 1L;
        var amount = new BigDecimal(550000);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()))
                .thenReturn(Optional.of(amount));

        var exception = assertThrows(WithdrawalLimitExceededException.class, () -> {
            transactionService.withdrawal(new WithdrawalRequest(amount), accountId);
        });

        assertEquals(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED, exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, times(1)).sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("이체하다")
    @Test
    void transfer() {
        var sendAccount = new Account("1234", new BigDecimal(3100000), "홍길동");
        var receiveAccount = new Account("1235", new BigDecimal(0), "홍길동");

        var sendAccountId = 1L;
        var receiveAccountId = 2L;
        var amount = new BigDecimal(500000);

        var sendTransaction = Transaction
                .builder()
                .account(sendAccount)
                .fee(new BigDecimal(5000))
                .amount(amount.negate())
                .type(TransactionType.TRANSFER_OUT)
                .build();

        var receiveTransaction = Transaction
                .builder()
                .account(receiveAccount)
                .amount(amount)
                .type(TransactionType.TRANSFER_IN)
                .build();

        when(accountRepository.findById(sendAccountId)).thenReturn(Optional.of(sendAccount));
        when(accountRepository.findByAccountNumber(receiveAccount.getAccountNumber())).thenReturn(Optional.of(receiveAccount));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(sendTransaction)
                .thenReturn(receiveTransaction);
        when(transactionRepository.sumTransfersForToday(sendAccount.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()))
                .thenReturn(Optional.of(amount));

        var response = transactionService.transfer(new TransferRequest(receiveAccount.getAccountNumber(), amount), sendAccountId);

        verify(accountRepository, times(1)).findById(sendAccountId);
        verify(accountRepository, times(1)).findByAccountNumber(receiveAccount.getAccountNumber());
        verify(transactionRepository, times(1)).sumTransfersForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @DisplayName("존재하지 않는 계좌로 이체 시")
    @Test
    void transfer_AccountNotFound() {
        var sendAccount = new Account("1234", new BigDecimal(3100000), "홍길동");
        var receiveAccount = new Account("1235", new BigDecimal(0), "홍길동");

        var sendAccountId = 1L;
        var receiveAccountId = 2L;
        var amount = new BigDecimal(500000);

        when(accountRepository.findById(sendAccountId)).thenReturn(Optional.of(sendAccount));
        when(accountRepository.findByAccountNumber(receiveAccount.getAccountNumber())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.transfer(new TransferRequest(receiveAccount.getAccountNumber(), amount), sendAccountId));

        assertEquals(ErrorMessage.ACCOUNT_NOT_EXISTS, exception.getMessage());
        verify(accountRepository, times(1)).findById(sendAccountId);
        verify(accountRepository, times(1)).findByAccountNumber(receiveAccount.getAccountNumber());
        verify(transactionRepository, never()).sumTransfersForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("이체할 금액보다 잔액이 적을 경우")
    @Test
    void transfer_InsufficientBalance() {
        var sendAccount = new Account("1234", new BigDecimal(400000), "홍길동");
        var receiveAccount = new Account("1235", new BigDecimal(0), "홍길동");

        var sendAccountId = 1L;
        var receiveAccountId = 2L;
        var amount = new BigDecimal(500000);

        when(accountRepository.findById(sendAccountId)).thenReturn(Optional.of(sendAccount));
        when(accountRepository.findByAccountNumber(receiveAccount.getAccountNumber())).thenReturn(Optional.of(receiveAccount));

        var exception = assertThrows(InsufficientBalanceException.class, () ->
                transactionService.transfer(new TransferRequest(receiveAccount.getAccountNumber(), amount), sendAccountId));

        assertEquals(ErrorMessage.INSUFFICIENT_BALANCE, exception.getMessage());
        verify(accountRepository, times(1)).findById(sendAccountId);
        verify(accountRepository, times(1)).findByAccountNumber(receiveAccount.getAccountNumber());
        verify(transactionRepository, never()).sumTransfersForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("당일 계좌이체 금액 한도 초과")
    @Test
    void transfer_LimitExceeded() {
        var sendAccount = new Account("1234", new BigDecimal(3100000), "홍길동");
        var receiveAccount = new Account("1235", new BigDecimal(0), "홍길동");

        var sendAccountId = 1L;
        var receiveAccountId = 2L;
        var amount = new BigDecimal(500000);

        when(accountRepository.findById(sendAccountId)).thenReturn(Optional.of(sendAccount));
        when(accountRepository.findByAccountNumber(receiveAccount.getAccountNumber())).thenReturn(Optional.of(receiveAccount));
        when(transactionRepository.sumTransfersForToday(sendAccount.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()))
                .thenReturn(Optional.of(new BigDecimal(2600000)));

        var exception = assertThrows(TransferLimitExceededException.class, () ->
                transactionService.transfer(new TransferRequest(receiveAccount.getAccountNumber(), amount), sendAccountId));

        assertEquals(ErrorMessage.TRANSFER_LIMIT_EXCEEDED, exception.getMessage());
        verify(accountRepository, times(1)).findById(sendAccountId);
        verify(accountRepository, times(1)).findByAccountNumber(receiveAccount.getAccountNumber());
        verify(transactionRepository, times(1)).sumTransfersForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @DisplayName("거래내역 조회")
    @Test
    void transactionHistory() {
        var id = 1L;
        var transactions = List.of(
                Transaction.builder()
                        .account(new Account("1234", new BigDecimal(10000), "홍길동"))
                        .amount(new BigDecimal(10000))
                        .afterAmount(new BigDecimal(10000))
                        .type(TransactionType.DEPOSIT)
                        .build(),
                Transaction.builder()
                        .account(new Account("1234", new BigDecimal(10000), "홍길동"))
                        .amount(new BigDecimal(10000))
                        .afterAmount(new BigDecimal(10000))
                        .targetName("고길동")
                        .targetAccountNumber("12345")
                        .type(TransactionType.TRANSFER_OUT)
                        .build()
        );

        when(transactionRepository.findByAccountTransactionHistory(any())).thenReturn(transactions);
        var response = transactionService.findTransactionHistory(id);

        assertEquals(response.getTransactionHistory().size(), 2);
        verify(transactionRepository, times(1)).findByAccountTransactionHistory(id);
    }
}