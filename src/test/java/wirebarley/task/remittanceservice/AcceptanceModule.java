package wirebarley.task.remittanceservice;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptanceModule {
    public static void 응답코드_확인(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static void 컨텐츠유형_확인(ExtractableResponse<Response> response, String contentType) {
        assertThat(response.header("Content-Type")).isEqualTo(contentType);
    }

    public static void URI_검증(ExtractableResponse<Response> response) {
        assertThat(response.header("Location")).isEqualTo("/accounts/1");
    }

    public static String headerLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }
}
