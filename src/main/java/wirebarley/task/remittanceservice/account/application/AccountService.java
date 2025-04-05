package wirebarley.task.remittanceservice.account.application;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.account.dto.AccountResponse;
import wirebarley.task.remittanceservice.util.exception.BalanceNotEmptyException;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse createAccount(AccountRequest request) {
        var account = accountRepository.save(request.toAccount());

        return AccountResponse.from(account);
    }

    public void deleteAccount(Long id) {
        var account = accountRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(ErrorMessage.ACCOUNT_NOT_EXISTS));
        if(!account.checkAmountExists()) {
            throw new BalanceNotEmptyException(ErrorMessage.BALANCE_NOT_EMPTY);
        }

        account.markAsDeleted();
    }
}
