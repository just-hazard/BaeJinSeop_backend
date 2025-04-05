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

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok().body(transactionService.deposit(request));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<WithdrawalResponse> withdrawal(@RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok().body(transactionService.withdrawal(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok().body(transactionService.transfer(request));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<TransactionHistoryResponse> findTransactionHistory(@PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.findTransactionHistory(id));
    }
}
