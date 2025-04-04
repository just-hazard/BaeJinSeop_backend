package wirebarley.task.remittanceservice.transaction.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @DisplayName("계좌에서 당일 출금된 금액 조회")
    @Test
    void findTotalWithdrawnToday() {
        var account = accountRepository.save(new Account("1234", new BigDecimal(10000)));
        var transaction1 = createTransactionInstance(account);
        var transaction2 = createTransactionInstance(account);
        var transaction3 = createTransactionInstance(account);

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));

        var totalWithdrawnToday = transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()).orElse(BigDecimal.ZERO).setScale(0, RoundingMode.FLOOR);
        assertEquals(new BigDecimal(300000), totalWithdrawnToday);
    }

    private Transaction createTransactionInstance(Account account) {
        return Transaction
                .builder()
                .account(account)
                .amount(new BigDecimal(100000))
                .type(TransactionType.WITHDRAWAL)
                .build();
    }
}