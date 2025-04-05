package wirebarley.task.remittanceservice.transaction.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.account.domain.AccountRepository;
import wirebarley.task.remittanceservice.util.BankUtil;

import java.math.BigDecimal;
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
        var account = accountRepository.save(new Account("1234", new BigDecimal(10000), "홍길동"));
        var transaction1 = createTransactionInstance(account, TransactionType.WITHDRAWAL, null);
        var transaction2 = createTransactionInstance(account, TransactionType.WITHDRAWAL, null);
        var transaction3 = createTransactionInstance(account, TransactionType.WITHDRAWAL, null);

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));

        var totalWithdrawnToday = transactionRepository.sumWithdrawalsForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()).orElse(BigDecimal.ZERO);
        assertEquals(new BigDecimal(300000), BankUtil.removeDecimalPoint(totalWithdrawnToday));
    }

    @DisplayName("계좌에서 당일 이체된 금액 조회")
    @Test
    void findTotalTransferToday() {
        var account = accountRepository.save(new Account("1234", new BigDecimal(10000),"홍길동"));
        var transaction1 = createTransactionInstance(account, TransactionType.TRANSFER_OUT, new BigDecimal(500));
        var transaction2 = createTransactionInstance(account, TransactionType.WITHDRAWAL, null);
        var transaction3 = createTransactionInstance(account, TransactionType.TRANSFER_OUT, new BigDecimal(300));
        var transaction4 = createTransactionInstance(account, TransactionType.TRANSFER_IN, null);

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3, transaction4));

        var totalTransferAmountToday = transactionRepository.sumTransfersForToday(account.getAccountNumber(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()).orElse(BigDecimal.ZERO);
        assertEquals(new BigDecimal(200000), BankUtil.removeDecimalPoint(totalTransferAmountToday));
    }

    @DisplayName("거래 내역 조회")
    @Test
    void findByAccountTransactionHistory() {
        var account = accountRepository.save(new Account("1234", new BigDecimal(10000),"홍길동"));
        var transaction1 = createTransactionInstance(account, TransactionType.TRANSFER_OUT, new BigDecimal(500));
        var transaction2 = createTransactionInstance(account, TransactionType.WITHDRAWAL, null);
        var transaction3 = createTransactionInstance(account, TransactionType.DEPOSIT, new BigDecimal(300));
        var transaction4 = createTransactionInstance(account, TransactionType.TRANSFER_IN, null);

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3, transaction4));

        var history = transactionRepository.findByAccountTransactionHistory(account.getId());
        assertEquals(history.size(), 4);
        assertEquals(history.get(0).getType(), TransactionType.TRANSFER_IN);
        assertEquals(history.get(1).getType(), TransactionType.DEPOSIT);
        assertEquals(history.get(2).getType(), TransactionType.WITHDRAWAL);
        assertEquals(history.get(3).getType(), TransactionType.TRANSFER_OUT);
    }

    private Transaction createTransactionInstance(Account account, TransactionType type, BigDecimal fee) {
        return Transaction
                .builder()
                .account(account)
                .fee(fee)
                .amount(new BigDecimal(100000))
                .type(type)
                .build();
    }
}