package wirebarley.task.remittanceservice.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wirebarley.task.remittanceservice.AcceptanceModule;
import wirebarley.task.remittanceservice.AcceptanceTest;
import wirebarley.task.remittanceservice.account.AccountRequestModule;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.transaction.dto.DepositRequest;
import wirebarley.task.remittanceservice.transaction.dto.TransferRequest;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalRequest;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("송금 기능")
public class TransactionAcceptanceTest extends AcceptanceTest {

    private AccountRequest request;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        request = new AccountRequest("1234", new BigDecimal(0), "홍길동");
    }

    @DisplayName("계좌에 입금한다")
    @Test
    void deposit() {
        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var depositRequest = new DepositRequest(
                new BigDecimal(10000)
        );

        var response = TransactionRequestModule.입금_요청(depositRequest, accountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.OK.value());
        TransactionRequestModule.입금_검증(request.getAccountNumber(), depositRequest, response);
    }

    @DisplayName("존재하지 않는 계좌에 입금 시도한다")
    @Test
    void notExistsAccount_Deposit() {
        var depositRequest = new DepositRequest(
                new BigDecimal(10000)
        );

        var response = TransactionRequestModule.입금_요청(depositRequest, 1L);
        AcceptanceModule.응답코드_확인(response, HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @DisplayName("계좌에서 출금한다")
    @Test
    void withdrawal() {
        request = new AccountRequest("1234", new BigDecimal(10000000), "홍길동");

        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var withdrawalRequest = new WithdrawalRequest(
                new BigDecimal(1000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest, accountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.OK.value());
        TransactionRequestModule.출금_검증(request, withdrawalRequest, response);
    }

    @DisplayName("존재하지 않는 계좌에 출금 시도한다")
    @Test
    void notExistsAccount_Withdrawal() {
        var withdrawalRequest = new WithdrawalRequest(
                new BigDecimal(1000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest, 1L);
        AcceptanceModule.응답코드_확인(response, HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @DisplayName("출금 시 잔액이 부족할 때")
    @Test
    void insufficientBalance_Withdrawal() {
        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var withdrawalRequest = new WithdrawalRequest(
                new BigDecimal(1000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest, accountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.INSUFFICIENT_BALANCE);
    }

    @DisplayName("당일 출금 한도 초과일 때")
    @Test
    void withdrawalLimitExceeded() {
        request = new AccountRequest("1234", new BigDecimal(10000000), "홍길동");

        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var withdrawalRequest = new WithdrawalRequest(
                new BigDecimal(2000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest, accountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.FORBIDDEN.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED);
    }

    @DisplayName("계좌 이체를 한다")
    @Test
    void transfer() {
        var sendAccountRequest = new AccountRequest("1234", new BigDecimal(10000000), "홍길동");
        var sendAccountResponse=
                AccountRequestModule.계좌_생성_요청(sendAccountRequest);

        var receiveAccountRequest = new AccountRequest("12345", new BigDecimal(0), "홍길동");
        var receiveAccountResponse=
                AccountRequestModule.계좌_생성_요청(receiveAccountRequest);

        var transferRequest = new TransferRequest(
                receiveAccountResponse.jsonPath().getString("accountNumber"),
                new BigDecimal(2000000)
        );

        var response = TransactionRequestModule.이체_요청(transferRequest, sendAccountResponse.jsonPath().getLong("id"));
        // fromAccount 검증
        assertEquals(1, response.jsonPath().getInt("fromAccount.accountId"));
        assertEquals("홍길동", response.jsonPath().getString("fromAccount.name"));
        assertEquals("1234", response.jsonPath().getString("fromAccount.accountNumber"));
        assertEquals(7980000, response.jsonPath().getInt("fromAccount.balance"));
        assertEquals(-2000000, response.jsonPath().getInt("fromAccount.amount"));
        assertEquals(20000, response.jsonPath().getInt("fromAccount.fee"));
        assertEquals("TRANSFER_OUT", response.jsonPath().getString("fromAccount.type"));
        assertEquals("홍길동", response.jsonPath().getString("fromAccount.targetName"));
        assertEquals("12345", response.jsonPath().getString("fromAccount.targetAccountNumber"));
        assertNull(response.jsonPath().get("fromAccount.memo"));
        assertNotNull(response.jsonPath().getString("fromAccount.date"));

        // toAccount 검증
        assertEquals(2, response.jsonPath().getInt("toAccount.accountId"));
        assertEquals("홍길동", response.jsonPath().getString("toAccount.name"));
        assertEquals("12345", response.jsonPath().getString("toAccount.accountNumber"));
        assertEquals(2000000, response.jsonPath().getInt("toAccount.balance"));
        assertEquals(2000000, response.jsonPath().getInt("toAccount.amount"));
        assertNull(response.jsonPath().get("toAccount.fee"));
        assertEquals("TRANSFER_IN", response.jsonPath().getString("toAccount.type"));
        assertEquals("홍길동", response.jsonPath().getString("toAccount.targetName"));
        assertEquals("1234", response.jsonPath().getString("toAccount.targetAccountNumber"));
        assertNull(response.jsonPath().get("toAccount.memo"));
        assertNotNull(response.jsonPath().getString("toAccount.date"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 계좌에 이체를 시도한다")
    @Test
    void notExistsAccount_transfer() {
        var sendAccountRequest = new AccountRequest("1234", new BigDecimal(10000000), "홍길동");
        var sendAccountResponse=
                AccountRequestModule.계좌_생성_요청(sendAccountRequest);

        var receiveAccountRequest = new AccountRequest("12345", new BigDecimal(0), "홍길동");
        var receiveAccountResponse=
                AccountRequestModule.계좌_생성_요청(receiveAccountRequest);

        var transferRequest = new TransferRequest(
                "1212",
                new BigDecimal(2000000)
        );

        var response = TransactionRequestModule.이체_요청(transferRequest, sendAccountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @DisplayName("이체 시 잔액이 부족할 때")
    @Test
    void insufficientBalance_transfer() {
        var sendAccountRequest = new AccountRequest("1234", new BigDecimal(0), "홍길동");
        var sendAccountResponse=
                AccountRequestModule.계좌_생성_요청(sendAccountRequest);

        var receiveAccountRequest = new AccountRequest("12345", new BigDecimal(0), "홍길동");
        var receiveAccountResponse=
                AccountRequestModule.계좌_생성_요청(receiveAccountRequest);

        var transferRequest = new TransferRequest(
                receiveAccountResponse.jsonPath().getString("accountNumber"),
                new BigDecimal(2000000)
        );

        var response = TransactionRequestModule.이체_요청(transferRequest, sendAccountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.INSUFFICIENT_BALANCE);
    }

    @DisplayName("당일 이체 한도 초과")
    @Test
    void limitExceeded_transfer() {
        var sendAccountRequest = new AccountRequest("1234", new BigDecimal(10000000), "홍길동");
        var sendAccountResponse=
                AccountRequestModule.계좌_생성_요청(sendAccountRequest);

        var receiveAccountRequest = new AccountRequest("12345", new BigDecimal(0), "홍길동");
        var receiveAccountResponse=
                AccountRequestModule.계좌_생성_요청(receiveAccountRequest);

        var transferRequest = new TransferRequest(
                receiveAccountResponse.jsonPath().getString("accountNumber"),
                new BigDecimal(3100000)
        );

        var response = TransactionRequestModule.이체_요청(transferRequest, sendAccountResponse.jsonPath().getLong("id"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.FORBIDDEN.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.TRANSFER_LIMIT_EXCEEDED);
    }

    @DisplayName("거래내역을 조회한다")
    @Test
    void transactionHistory() {
        var sendAccountRequest = new AccountRequest("1234", new BigDecimal(10000000), "홍길동");
        var sendAccountResponse=
                AccountRequestModule.계좌_생성_요청(sendAccountRequest);

        var receiveAccountRequest = new AccountRequest("12345", new BigDecimal(0), "홍길동");
        var receiveAccountResponse=
                AccountRequestModule.계좌_생성_요청(receiveAccountRequest);

        var depositRequest = new DepositRequest(
                new BigDecimal(10000)
        );

        TransactionRequestModule.입금_요청(depositRequest, sendAccountResponse.jsonPath().getLong("id"));

        var withdrawalRequest = new WithdrawalRequest(
                new BigDecimal(1000000)
        );

        TransactionRequestModule.출금_요청(withdrawalRequest, sendAccountResponse.jsonPath().getLong("id"));


        var transferRequest = new TransferRequest(
                receiveAccountResponse.jsonPath().getString("accountNumber"),
                new BigDecimal(2000000)
        );

        TransactionRequestModule.이체_요청(transferRequest, sendAccountResponse.jsonPath().getLong("id"));

        var response = TransactionRequestModule.내역_조회_요청(sendAccountResponse.jsonPath().getLong("id"));

        List<Map<String, Object>> history = response.jsonPath().getList("transactionHistory");
        assertEquals(3, history.size());

        Map<String, Object> data = history.get(0);
        assertEquals(1, data.get("accountId"));
        assertEquals("홍길동", data.get("name"));
        assertEquals("1234", data.get("accountNumber"));
        assertEquals(6990000, ((Number) data.get("balance")).intValue());
        assertEquals(-2000000, ((Number) data.get("amount")).intValue());
        assertEquals(20000, ((Number) data.get("fee")).intValue());
        assertEquals("TRANSFER_OUT", data.get("type"));
        assertEquals("홍길동", data.get("targetName"));
        assertEquals("12345", data.get("targetAccountNumber"));
        assertNull(data.get("memo"));
        assertNotNull(data.get("date"));
        AcceptanceModule.응답코드_확인(response, HttpStatus.OK.value());
    }
}
