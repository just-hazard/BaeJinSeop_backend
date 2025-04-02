package wirebarley.task.remittanceservice.account.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wirebarley.task.remittanceservice.account.application.AccountService;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.account.dto.AccountResponse;

import java.net.URI;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        var account = accountService.createAccount(request);
        return ResponseEntity.created(URI.create("/accounts/" + account.getId())).body(account);
    }
}
