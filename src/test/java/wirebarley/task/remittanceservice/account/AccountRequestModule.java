package wirebarley.task.remittanceservice.account;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wirebarley.task.remittanceservice.account.dto.AccountRequest;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AccountRequestModule {

    public static ExtractableResponse<Response> 계좌_생성_요청(AccountRequest accountRequest) {
        return given()
                .body(accountRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/accounts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 계좌_삭제_요청(String location) {
        return given()
                .when()
                .delete(location)
                .then().log().all()
                .extract();
    }

    public static void 계좌_생성_검증(AccountRequest request, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(1),
                () -> assertThat(response.jsonPath().getString("accountNumber")).isEqualTo(request.getAccountNumber()),
                () -> assertThat(response.jsonPath().getObject("balance", BigDecimal.class)).isEqualTo(request.getBalance()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(request.getName()),
                () -> assertThat(response.jsonPath().getString("createdDate")).isNotNull()
        );
    }

    public static String headerLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }
}
