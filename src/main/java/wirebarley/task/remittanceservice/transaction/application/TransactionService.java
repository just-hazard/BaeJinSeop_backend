package wirebarley.task.remittanceservice.transaction.application;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.transaction.domain.Transaction;
import wirebarley.task.remittanceservice.transaction.domain.TransactionRepository;
import wirebarley.task.remittanceservice.transaction.domain.TransactionType;
import wirebarley.task.remittanceservice.transaction.dto.DepositRequest;
import wirebarley.task.remittanceservice.transaction.dto.DepositResponse;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalRequest;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalResponse;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;
import wirebarley.task.remittanceservice.util.exception.InsufficientBalanceException;
import wirebarley.task.remittanceservice.util.exception.WithdrawalLimitExceededException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public DepositResponse deposit(DepositRequest request) {
        var account = findAccount(request.getAccountId());
        account.depositAmount(request.getAmount());
        return DepositResponse.from(
                transactionRepository.save(
                    Transaction
                            .builder()
                            .account(account)
                            .amount(request.getAmount())
                            .type(TransactionType.DEPOSIT)
                            .build()
                )
        );
    }

    public WithdrawalResponse withdrawal(WithdrawalRequest request) {
        var account = findAccount(request.getAccountId());
        if (account.compareBalance(request.getAmount())) {
            throw new InsufficientBalanceException(ErrorMessage.INSUFFICIENT_BALANCE);
        }

        var totalWithdrawnToday = transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .orElse(BigDecimal.ZERO).setScale(0, RoundingMode.FLOOR);

        checkWithdrawalLimit(totalWithdrawnToday, request.getAmount());

        account.withdrawal(request.getAmount());

        return WithdrawalResponse.from(
            transactionRepository.save(
                Transaction
                    .builder()
                    .account(account)
                    .amount(request.getAmount())
                    .type(TransactionType.WITHDRAWAL)
                    .build()
            )
        );
    }

    private void checkWithdrawalLimit(BigDecimal totalWithdrawnToday, BigDecimal amount) {
        final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("1000000");
        if (totalWithdrawnToday.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new WithdrawalLimitExceededException(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED);
        }
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ACCOUNT_NOT_EXISTS));
    }
}
