package wirebarley.task.remittanceservice.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @DisplayName("정적 팩토리 메서드")
    @Test
    void accountStaticFactoryMethod() {
        var accountNumber = "1234";
        var balance = new BigDecimal(100000);

        account = Account.of(accountNumber, balance);
        assertAll(
            () -> assertThat(account.getAccountNumber()).isEqualTo(accountNumber),
            () -> assertThat(account.getBalance()).isEqualTo(balance)
        );
    }

    @DisplayName("금액이 0원일 경우")
    @Test
    void checkAmountExists_return_true() {
        account = Account.of("1234", new BigDecimal(0));

        assertTrue(account.checkAmountExists());
    }

    @DisplayName("금액이 0원보다 클 경우")
    @Test
    void checkAmountExists_return_false() {
        account = Account.of("1234", new BigDecimal(10000));

        assertFalse(account.checkAmountExists());
    }

    @DisplayName("입금된 금액을 더하다")
    @Test
    void depositAmount() {
        account = Account.of("1234", new BigDecimal(10000));
        account.depositAmount(new BigDecimal(10000));
        assertThat(account.getBalance()).isEqualTo(new BigDecimal(20000));
    }
}