package chocolate.gametask.repository;
import chocolate.gametask.entity.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Long> {
    List<LeaderboardEntry> findByMonthYearOrderByTotalBonusEarnedDesc(String monthYear);
    void deleteByMonthYear(String monthYear);
}
