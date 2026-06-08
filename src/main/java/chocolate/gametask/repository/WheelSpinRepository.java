package chocolate.gametask.repository;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.WheelSpin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
@Repository
public interface WheelSpinRepository extends JpaRepository<WheelSpin, Long> {

    @Query("SELECT COUNT(w) FROM WheelSpin w WHERE w.user = :user AND w.isFree = true AND w.spunAt >= :since")
    long countFreeSpinsSince(@Param("user") User user, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(w) FROM WheelSpin w WHERE w.user = :user AND w.isFree = false AND w.spunAt >= :since")
    long countPaidSpinsSince(@Param("user") User user, @Param("since") LocalDateTime since);
}