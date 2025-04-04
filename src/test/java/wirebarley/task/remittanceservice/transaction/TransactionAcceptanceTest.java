package wirebarley.task.remittanceservice.transaction;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wirebarley.task.remittanceservice.AcceptanceTest;
import wirebarley.task.remittanceservice.account.AccountRequestModule;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.transaction.dto.DepositRequest;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalRequest;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

import java.math.BigDecimal;

@DisplayName("송금 기능")
public class TransactionAcceptanceTest extends AcceptanceTest {

    private AccountRequest request;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        request = new AccountRequest("1234", new BigDecimal(0));
    }

    @DisplayName("계좌에 입금한다")
    @Test
    void deposit() {
        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var depositRequest = new DepositRequest(
                accountResponse.jsonPath().getLong("id"),
                new BigDecimal(10000)
        );

        var response = TransactionRequestModule.입금_요청(depositRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.OK.value());
        TransactionRequestModule.입금_검증(request.getAccountNumber(), depositRequest, response);
    }

    @DisplayName("존재하지 않는 계좌에 입금 시도한다")
    @Test
    void notExistsAccount_Deposit() {
        var depositRequest = new DepositRequest(
                1L,
                new BigDecimal(10000)
        );

        var response = TransactionRequestModule.입금_요청(depositRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        AssertionsForClassTypes.assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @DisplayName("계좌에서 출금한다")
    @Test
    void withdrawal() {
        request = new AccountRequest("1234", new BigDecimal(10000000));

        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var withdrawalRequest = new WithdrawalRequest(
                accountResponse.jsonPath().getLong("id"),
                new BigDecimal(1000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 계좌에 출금 시도한다")
    @Test
    void notExistsAccount_Withdrawal() {
        var withdrawalRequest = new WithdrawalRequest(
                1L,
                new BigDecimal(1000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        AssertionsForClassTypes.assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @DisplayName("출금 시 잔액이 부족할 때")
    @Test
    void insufficientBalance_Withdrawal() {
        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var withdrawalRequest = new WithdrawalRequest(
                accountResponse.jsonPath().getLong("id"),
                new BigDecimal(1000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        AssertionsForClassTypes.assertThat(response.body().asString()).isEqualTo(ErrorMessage.INSUFFICIENT_BALANCE);
    }

    @DisplayName("당일 출금 한도 초과일 때")
    @Test
    void withdrawalLimitExceeded() {
        request = new AccountRequest("1234", new BigDecimal(10000000));

        var accountResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var withdrawalRequest = new WithdrawalRequest(
                accountResponse.jsonPath().getLong("id"),
                new BigDecimal(2000000)
        );

        var response = TransactionRequestModule.출금_요청(withdrawalRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        AssertionsForClassTypes.assertThat(response.body().asString()).isEqualTo(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED);
    }
}
