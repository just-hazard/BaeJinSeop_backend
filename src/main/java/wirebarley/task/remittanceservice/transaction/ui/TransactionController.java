package wirebarley.task.remittanceservice.transaction.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wirebarley.task.remittanceservice.transaction.application.TransactionService;
import wirebarley.task.remittanceservice.transaction.dto.*;

@RestController
@RequestMapping("/accounts")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.deposit(request, id));
    }

    @PostMapping("/{id}/withdrawal")
    public ResponseEntity<WithdrawalResponse> withdrawal(@RequestBody WithdrawalRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.withdrawal(request, id));
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.transfer(request, id));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<TransactionHistoryResponse> findTransactionHistory(@PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.findTransactionHistory(id));
    }
}
