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
        var name = "홍길동";

        account = Account.of(accountNumber, balance, name);
        assertAll(
            () -> assertThat(account.getAccountNumber()).isEqualTo(accountNumber),
            () -> assertThat(account.getBalance()).isEqualTo(balance),
            () -> assertThat(account.getName()).isEqualTo(name)
        );
    }

    @DisplayName("금액이 0원일 경우")
    @Test
    void checkAmountExists_return_true() {
        account = Account.of("1234", new BigDecimal(0), "홍길동");

        assertTrue(account.checkAmountExists());
    }

    @DisplayName("금액이 0원보다 클 경우")
    @Test
    void checkAmountExists_return_false() {
        account = Account.of("1234", new BigDecimal(10000), "홍길동");

        assertFalse(account.checkAmountExists());
    }

    @DisplayName("입금된 금액을 더하다")
    @Test
    void depositAmount() {
        account = Account.of("1234", new BigDecimal(10000), "홍길동");
        account.deposit(new BigDecimal(10000));
        assertThat(account.getBalance()).isEqualTo(new BigDecimal(20000));
    }

    @DisplayName("송금할 금액이 통장 잔고와 비교해서 0보다 큰지 비교하다")
    @Test
    void compareBalance() {
        account = Account.of("1234", new BigDecimal(10000), "홍길동");
        assertTrue(account.compareBalance(new BigDecimal(11000)));
        account.deposit(new BigDecimal(10000));
        assertFalse(account.compareBalance(new BigDecimal(11000)));
    }

    @DisplayName("출금하다")
    @Test
    void withdrawal() {
        account = Account.of("1234", new BigDecimal(10000), "홍길동");
        account.withdrawal(new BigDecimal(5000));
        assertEquals(account.getBalance(), new BigDecimal(5000));
    }
}