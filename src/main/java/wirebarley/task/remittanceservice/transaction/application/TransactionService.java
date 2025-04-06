package wirebarley.task.remittanceservice.transaction.application;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.transaction.domain.TransactionRepository;
import wirebarley.task.remittanceservice.transaction.domain.TransactionType;
import wirebarley.task.remittanceservice.transaction.dto.*;
import wirebarley.task.remittanceservice.util.BankUtil;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;
import wirebarley.task.remittanceservice.util.exception.InsufficientBalanceException;
import wirebarley.task.remittanceservice.util.exception.TransferLimitExceededException;
import wirebarley.task.remittanceservice.util.exception.WithdrawalLimitExceededException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DepositResponse deposit(DepositRequest request, Long id) {
        var account = findAccount(id);
        account.deposit(request.getAmount());
        return DepositResponse.from(
                transactionRepository.save(
                    Transaction
                            .builder()
                            .account(account)
                            .amount(request.getAmount())
                            .afterAmount(account.getBalance())
                            .type(TransactionType.DEPOSIT)
                            .build()
                )
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WithdrawalResponse withdrawal(WithdrawalRequest request, Long id) {
        var account = findAccount(id);
        validateEnoughAmount(request.getAmount(), account);

        var totalWithdrawnToday = transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .orElse(BigDecimal.ZERO);

        validateWithdrawalLimit(BankUtil.removeDecimalPoint(totalWithdrawnToday), request.getAmount());

        account.withdrawal(request.getAmount());

        return WithdrawalResponse.from(
            transactionRepository.save(
                Transaction
                    .builder()
                    .account(account)
                    .amount(request.getAmount().negate())
                    .afterAmount(account.getBalance())
                    .type(TransactionType.WITHDRAWAL)
                    .build()
            )
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferResponse transfer(TransferRequest request, Long id) {
        var fromAccount = findAccount(id);
        var toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ACCOUNT_NOT_EXISTS));

        var fee = BankUtil.calculateTransferFee(request.getAmount());
        var totalAmount = request.getAmount().add(fee);

        validateEnoughAmount(request.getAmount(), fromAccount);

        var totalTransferToday = transactionRepository.sumTransfersForToday(fromAccount.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .orElse(BigDecimal.ZERO);

        validateTransferLimit(BankUtil.removeDecimalPoint(totalTransferToday), request.getAmount());

        fromAccount.withdrawal(totalAmount);
        toAccount.deposit(request.getAmount());

        return TransferResponse.of(
                transactionRepository.save(
                        Transaction
                                .builder()
                                .account(fromAccount)
                                .amount(request.getAmount().negate())
                                .fee(fee)
                                .targetName(toAccount.getName())
                                .targetAccountNumber(toAccount.getAccountNumber())
                                .afterAmount(fromAccount.getBalance())
                                .type(TransactionType.TRANSFER_OUT)
                                .build()
                ),
                transactionRepository.save(
                        Transaction
                                .builder()
                                .account(toAccount)
                                .amount(request.getAmount())
                                .targetName(fromAccount.getName())
                                .targetAccountNumber(fromAccount.getAccountNumber())
                                .afterAmount(toAccount.getBalance())
                                .type(TransactionType.TRANSFER_IN)
                                .build()
                )
        );
    }

    @Transactional(readOnly = true)
    public TransactionHistoryResponse findTransactionHistory(Long accountId) {
        return TransactionHistoryResponse.from(transactionRepository.findByAccountTransactionHistory(accountId));
    }

    private void validateEnoughAmount(BigDecimal amount, Account account) {
        if (account.compareBalance(amount)) {
            throw new InsufficientBalanceException(ErrorMessage.INSUFFICIENT_BALANCE);
        }
    }

    private void validateWithdrawalLimit(BigDecimal totalWithdrawnToday, BigDecimal amount) {
        final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("1000000");
        if (totalWithdrawnToday.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new WithdrawalLimitExceededException(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED);
        }
    }

    private void validateTransferLimit(BigDecimal totalTransferToday, BigDecimal amount) {
        final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("3000000");
        if (totalTransferToday.add(amount).compareTo(DAILY_TRANSFER_LIMIT) > 0) {
            throw new TransferLimitExceededException(ErrorMessage.TRANSFER_LIMIT_EXCEEDED);
        }
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ACCOUNT_NOT_EXISTS));
    }
}
