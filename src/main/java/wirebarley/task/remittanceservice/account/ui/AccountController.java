package wirebarley.task.remittanceservice.account.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
