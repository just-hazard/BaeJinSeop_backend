package wirebarley.task.remittanceservice.transaction;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;
import wirebarley.task.remittanceservice.transaction.domain.TransactionType;
import wirebarley.task.remittanceservice.transaction.dto.DepositRequest;
import wirebarley.task.remittanceservice.transaction.dto.TransferRequest;
import wirebarley.task.remittanceservice.transaction.dto.WithdrawalRequest;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TransactionRequestModule {

    public static ExtractableResponse<Response> 입금_요청(DepositRequest depositRequest, Long id) {
        return given()
                .body(depositRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/accounts/{id}/deposit", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 출금_요청(WithdrawalRequest withdrawalRequest, Long id) {
        return given()
                .body(withdrawalRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/accounts/{id}/withdrawal", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 이체_요청(TransferRequest transferRequest, Long id) {
        return given()
                .body(transferRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/accounts/{id}/transfer", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 내역_조회_요청(Long id) {
        return given()
                .when()
                .get("/accounts/{id}/transactions", id)
                .then().log().all()
                .extract();
    }

    public static void 입금_검증(String accountNumber, DepositRequest depositRequest, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.jsonPath().getLong("accountId")).isEqualTo(1),
                () -> assertThat(response.jsonPath().getString("accountNumber")).isEqualTo(accountNumber),
                () -> assertThat(response.jsonPath().getObject("balance", BigDecimal.class)).isEqualTo(depositRequest.getAmount()),
                () -> assertThat(response.jsonPath().getObject("amount", BigDecimal.class)).isEqualTo(depositRequest.getAmount()),
                () -> assertThat(response.jsonPath().getString("type")).isEqualTo(TransactionType.DEPOSIT.name())
        );
    }

    public static void 출금_검증(AccountRequest request, WithdrawalRequest withdrawalRequest, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.jsonPath().getLong("accountId")).isEqualTo(1),
                () -> assertThat(response.jsonPath().getString("accountNumber")).isEqualTo(request.getAccountNumber()),
                () -> assertThat(response.jsonPath().getObject("balance", BigDecimal.class)).isEqualTo(request.getBalance().subtract(withdrawalRequest.getAmount())),
                () -> assertThat(response.jsonPath().getObject("amount", BigDecimal.class)).isEqualTo(withdrawalRequest.getAmount().negate()),
                () -> assertThat(response.jsonPath().getString("type")).isEqualTo(TransactionType.WITHDRAWAL.name())
        );
    }
}
