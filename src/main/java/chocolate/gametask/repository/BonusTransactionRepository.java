package chocolate.gametask.repository;




import chocolate.gametask.entity.BonusTransaction;
import chocolate.gametask.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface BonusTransactionRepository extends JpaRepository<BonusTransaction, Long> {

    @Query("SELECT t FROM BonusTransaction t WHERE t.user = :user AND t.transactionType = 'CREDIT' " +
            "AND (t.expiresAt IS NULL OR t.expiresAt > :now) AND t.amount > 0 ORDER BY t.expiresAt ASC, t.id ASC")
    List<BonusTransaction> findActiveCreditsFifo(@Param("user") User user, @Param("now") LocalDateTime now);

    List<BonusTransaction> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM BonusTransaction t " +
            "WHERE t.user = :user AND t.transactionType = 'CREDIT' AND (t.expiresAt IS NULL OR t.expiresAt > :now)")
    Integer calculateActiveBalance(@Param("user") User user, @Param("now") LocalDateTime now);
}
