package wirebarley.task.remittanceservice.account.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wirebarley.task.remittanceservice.account.application.AccountService;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.account.dto.AccountResponse;

import java.net.URI;

@RestController
@Tag(name = "계좌 API", description = "계좌 관련 API 목록입니다")
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "계좌 생성", description = "계좌번호, 이름, 초기 잔액을 입력받아 새로운 계좌를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "계좌 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복되는 계좌번호 등록 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "이미 존재하는 계좌 예시",
                    summary = "동일한 계좌 번호로 생성 요청 시",
                    value = "이미 데이터가 존재합니다"
            )))
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        var account = accountService.createAccount(request);
        return ResponseEntity.created(URI.create("/accounts/" + account.getId())).body(account);
    }

    @Operation(summary = "계좌 삭제", description = "계좌 ID를 통해 잔액이 0원인 계좌를 삭제 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "계좌 삭제 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "계좌가 존재하지 않음", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "존재하지 않는 계좌 예시",
                    summary = "존재하지 않는 계좌 조회 요청 시",
                    value = "계좌가 존재하지 않습니다"
            ))),
            @ApiResponse(responseCode = "400", description = "잔액이 남아 있어 삭제 불가능", content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    name = "잔액이 남아 있는 계좌 삭제 예시",
                    summary = "잔액이 남아 있는 계좌 삭제 요청 시",
                    value = "금액이 존재하므로 계좌를 삭제 할 수 없습니다"
            )))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
