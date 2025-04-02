package wirebarley.task.remittanceservice.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wirebarley.task.remittanceservice.AcceptanceTest;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.util.exception.ErrorMessage;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("계좌 기능")
public class AccountAcceptanceTest extends AcceptanceTest {

    private AccountRequest request;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        request = new AccountRequest("1234", new BigDecimal(0));
    }

    @DisplayName("계좌를 생성한다")
    @Test
    void createAccount() {
        var response=
                AccountRequestModule.계좌_생성_요청(request);

        AccountRequestModule.응답코드_확인(response, HttpStatus.CREATED.value());
        AccountRequestModule.URI_검증(response);
        AccountRequestModule.컨텐츠유형_확인(response, MediaType.APPLICATION_JSON_VALUE);
        AccountRequestModule.계좌_생성_검증(request, response);
    }

    @DisplayName("중복된 계좌번호 생성")
    @Test
    void duplicateAccount() {
        AccountRequestModule.계좌_생성_요청(request);
        var response = AccountRequestModule.계좌_생성_요청(request);

        AccountRequestModule.응답코드_확인(response, HttpStatus.CONFLICT.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.DATA_ALREADY_EXISTS);
    }

    @DisplayName("계좌를 삭제한다")
    @Test
    void removeAccount() {
        var createResponse=
                AccountRequestModule.계좌_생성_요청(request);

        var response = AccountRequestModule.계좌_삭제_요청(AccountRequestModule.headerLocation(createResponse));

        AccountRequestModule.응답코드_확인(response, HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 계좌를 삭제한다")
    @Test
    void notExistsRemoveAccount() {
        var response = AccountRequestModule.계좌_삭제_요청("/accounts/1");

        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @DisplayName("잔액이 있는 계좌를 삭제한다")
    @Test
    void deleteAnAccountWithBalance() {
        request.setBalance(new BigDecimal(10000));
        var createResponse=
                AccountRequestModule.계좌_생성_요청(request);
        var response = AccountRequestModule.계좌_삭제_요청(AccountRequestModule.headerLocation(createResponse));
        AccountRequestModule.응답코드_확인(response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.BALANCE_NOT_EMPTY);
    }
}
