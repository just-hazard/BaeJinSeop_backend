package wirebarley.task.remittanceservice.transaction.application;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
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

    public DepositResponse deposit(DepositRequest request) {
        var account = findAccount(request.getAccountId());
        account.deposit(request.getAmount());
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
        confirmEnoughAmount(request.getAmount(), account);

        var totalWithdrawnToday = transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .orElse(BigDecimal.ZERO);

        checkWithdrawalLimit(BankUtil.removeDecimalPoint(totalWithdrawnToday), request.getAmount());

        account.withdrawal(request.getAmount());

        return WithdrawalResponse.from(
            transactionRepository.save(
                Transaction
                    .builder()
                    .account(account)
                    .amount(request.getAmount().negate())
                    .type(TransactionType.WITHDRAWAL)
                    .build()
            )
        );
    }

    public TransferResponse transfer(TransferRequest request) {
        var fromAccount = findAccount(request.getFromAccountId());
        var toAccount = findAccount(request.getToAccountId());

        var fee = BankUtil.calculateTransferFee(request.getAmount());
        var totalAmount = request.getAmount().add(fee);

        confirmEnoughAmount(request.getAmount(), fromAccount);

        var totalTransferToday = transactionRepository.sumTransfersForToday(fromAccount.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .orElse(BigDecimal.ZERO);

        checkTransferLimit(BankUtil.removeDecimalPoint(totalTransferToday), request.getAmount());

        fromAccount.withdrawal(totalAmount);
        toAccount.deposit(request.getAmount());

        return TransferResponse.of(
                transactionRepository.save(
                        Transaction
                                .builder()
                                .account(fromAccount)
                                .amount(request.getAmount().negate())
                                .fee(fee)
                                .type(TransactionType.SEND_TRANSFER)
                                .build()
                ),
                transactionRepository.save(
                        Transaction
                                .builder()
                                .account(toAccount)
                                .amount(request.getAmount())
                                .type(TransactionType.RECEIVE_TRANSFER)
                                .build()
                )
        );
    }

    private void confirmEnoughAmount(BigDecimal amount, Account account) {
        if (account.compareBalance(amount)) {
            throw new InsufficientBalanceException(ErrorMessage.INSUFFICIENT_BALANCE);
        }
    }

    private void checkWithdrawalLimit(BigDecimal totalWithdrawnToday, BigDecimal amount) {
        final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("1000000");
        if (totalWithdrawnToday.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new WithdrawalLimitExceededException(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED);
        }
    }

    private void checkTransferLimit(BigDecimal totalTransferToday, BigDecimal amount) {
        final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("3000000");
        if (totalTransferToday.add(amount).compareTo(DAILY_TRANSFER_LIMIT) > 0) {
            throw new TransferLimitExceededException(ErrorMessage.TRANSFER_LIMIT_EXCEEDED);
        }
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ACCOUNT_NOT_EXISTS));
    }
}
