package wirebarley.task.remittanceservice.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @DisplayName("중복된 계좌번호 등록")
    @Test
    void duplicateAccount() {
        var account = new Account("1234", new BigDecimal(10000));
        var account1 = new Account("1234", new BigDecimal(10000));
        accountRepository.save(account);

        assertThrows(DataIntegrityViolationException.class, () -> {
            accountRepository.save(account1);
        });
    }
}