package wirebarley.task.remittanceservice.transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.account.accountNumber = :accountNumber " +
            "AND t.type = 'WITHDRAWAL' " +
            "AND t.createdDate BETWEEN :start AND :end")
    Optional<BigDecimal> sumWithdrawalsForToday(
            @Param("accountNumber") String accountNumber,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.account.accountNumber = :accountNumber " +
            "AND t.type = 'TRANSFER_OUT' " +
            "AND t.createdDate BETWEEN :start AND :end")
    Optional<BigDecimal> sumTransfersForToday(
            @Param("accountNumber") String accountNumber,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.account WHERE t.account.id = :accountId ORDER BY t.createdDate DESC")
    List<Transaction> findByAccountTransactionHistory(Long accountId);
}
