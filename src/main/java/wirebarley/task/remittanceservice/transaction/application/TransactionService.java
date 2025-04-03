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
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

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
        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(()-> new EntityNotFoundException(ErrorMessage.ACCOUNT_NOT_EXISTS));
        account.depositAmount(request.getAmount());
        return DepositResponse.from(transactionRepository.save(
                Transaction
                .builder()
                .account(account)
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .build()
        ));
    }
}
