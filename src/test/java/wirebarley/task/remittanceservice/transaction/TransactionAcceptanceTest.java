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
        TransactionRequestModule.입금_검증(request.getAccountNumber(), depositRequest, response);
    }

    @DisplayName("존재하지 않는 계좌에 입금 시도한다")
    @Test
    void notExistsAccount() {
        var depositRequest = new DepositRequest(
                1L,
                new BigDecimal(10000)
        );

        var response = TransactionRequestModule.입금_요청(depositRequest);
        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        AssertionsForClassTypes.assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }
}
