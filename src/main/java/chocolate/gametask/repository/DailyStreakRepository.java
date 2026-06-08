package chocolate.gametask.repository;
import chocolate.gametask.entity.DailyStreak;
import chocolate.gametask.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DailyStreakRepository extends JpaRepository<DailyStreak, Long> {
    Optional<DailyStreak> findByUser(User user);
}