package wirebarley.task.remittanceservice.account.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.account.dto.AccountResponse;

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
}
