package wirebarley.task.remittanceservice.transaction.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wirebarley.task.remittanceservice.transaction.application.TransactionService;
import wirebarley.task.remittanceservice.transaction.dto.*;

@RestController
@Tag(name = "송금 API", description = "입금 / 출금 / 이체 / 계좌 거래 내역조회 API 목록입니다")
@RequestMapping("/accounts")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "입금", description = "해당 계좌에 금액을 입금합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepositResponse.class))),
            @ApiResponse(responseCode = "404", description = "계좌가 존재하지 않음", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "존재하지 않는 계좌 예시",
                    summary = "존재하지 않는 계좌 조회 요청 시",
                    value = "계좌가 존재하지 않습니다"
            )))
    })
    @PostMapping("/{id}/deposit")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.deposit(request, id));
    }

    @Operation(summary = "출금", description = "해당 계좌에서 금액을 출금합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithdrawalResponse.class))),
            @ApiResponse(responseCode = "404", description = "계좌가 존재하지 않음", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "존재하지 않는 계좌 예시",
                    summary = "존재하지 않는 계좌 조회 요청 시",
                    value = "계좌가 존재하지 않습니다"
            ))),
            @ApiResponse(responseCode = "400", description = "잔액이 부족함", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "계좌에서 출금 시 잔액이 부족할 때 예시",
                    summary = "계좌에 잔액보다 많은 출금 금액 요청 시",
                    value = "잔액이 부족합니다"
            ))),
            @ApiResponse(responseCode = "403", description = "당일 출금 한도 초과", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "계좌에서 당일 출금 한도 초과 예시",
                    summary = "계좌에서 당일 출금 한도보다 많은 금액 출금 요청 시",
                    value = "당일 출금 한도를 초과하였습니다"
            )))
    })
    @PostMapping("/{id}/withdrawal")
    public ResponseEntity<WithdrawalResponse> withdrawal(@RequestBody WithdrawalRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.withdrawal(request, id));
    }

    @Operation(summary = "이체", description = "해당 계좌에서 다른 계좌로 금액을 이체합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이체 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "404", description = "계좌가 존재하지 않음", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "존재하지 않는 계좌 예시",
                    summary = "존재하지 않는 계좌 조회 요청 시",
                    value = "계좌가 존재하지 않습니다"
            ))),
            @ApiResponse(responseCode = "400", description = "잔액이 부족함", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "계좌에서 출금 시 잔액이 부족할 때 예시",
                    summary = "계좌에 잔액보다 많은 출금 금액 요청 시",
                    value = "잔액이 부족합니다"
            ))),
            @ApiResponse(responseCode = "403", description = "당일 이체 한도 초과", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "계좌에서 당일 이체 한도 초과 예시",
                    summary = "계좌에서 당일 이체 한도보다 많은 금액 이체 요청 시",
                    value = "당일 이체 한도를 초과하였습니다"
            )))
    })
    @PostMapping("/{id}/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.transfer(request, id));
    }

    @Operation(summary = "거래 내역 조회", description = "해당 계좌에 거래 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계좌 내역 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionHistoryResponse.class))),
    })
    @GetMapping("/{id}/transactions")
    public ResponseEntity<TransactionHistoryResponse> findTransactionHistory(@PathVariable Long id) {
        return ResponseEntity.ok().body(transactionService.findTransactionHistory(id));
    }
}
