package wirebarley.task.remittanceservice.transaction.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wirebarley.task.remittanceservice.transaction.application.TransactionService;
import wirebarley.task.remittanceservice.transaction.dto.DepositRequest;
import wirebarley.task.remittanceservice.transaction.dto.DepositResponse;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalRequest;

@RestController
@RequestMapping("/accounts")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok().body(transactionService.deposit(request));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<?> withdrawal(@RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok().body(transactionService.withdrawal(request));
    }
}
