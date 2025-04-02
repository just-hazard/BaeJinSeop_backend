package wirebarley.task.remittanceservice.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account 계좌;

    @DisplayName("정적 팩토리 메서드")
    @Test
    void accountStaticFactoryMethod() {
        var 계좌번호 = "1234";
        var 금액 = new BigDecimal(100000);

        계좌 = Account.of(계좌번호, 금액);
        assertAll(
            () -> assertThat(계좌.getAccountNumber()).isEqualTo(계좌번호),
            () -> assertThat(계좌.getBalance()).isEqualTo(금액)
        );
    }
}