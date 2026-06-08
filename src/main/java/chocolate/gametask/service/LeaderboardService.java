package chocolate.gametask.service;
import chocolate.gametask.dto.LeaderboardEntryDTO;
import chocolate.gametask.entity.LeaderboardEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.LeaderboardRepository;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getMonthly(String monthYear) {
        if (monthYear == null) {
            monthYear = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        AtomicInteger rank = new AtomicInteger(1);
        final String my = monthYear;
        return leaderboardRepository.findByMonthYearOrderByTotalBonusEarnedDesc(my).stream()
                .map(e -> LeaderboardEntryDTO.builder()
                        .rank(rank.getAndIncrement())
                        .username(e.getUsername())
                        .totalBonusEarned(e.getTotalBonusEarned())
                        .league(e.getLeague())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void calculateMonthlyLeaderboard() {
        String prevMonth = YearMonth.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<LeaderboardEntry> entries = leaderboardRepository
                .findByMonthYearOrderByTotalBonusEarnedDesc(prevMonth);

        int total = entries.size();
        for (int i = 0; i < total; i++) {
            LeaderboardEntry e = entries.get(i);
            double percentile = (double) i / total;
            String newLeague;
            if (percentile < 0.10) newLeague = "DIAMOND";
            else if (percentile < 0.25) newLeague = "PLATINUM";
            else if (percentile < 0.50) newLeague = "GOLD";
            else if (percentile < 0.75) newLeague = "SILVER";
            else newLeague = "BRONZE";
            e.setLeague(newLeague);
        }
        leaderboardRepository.saveAll(entries);
        log.info("Пересчитано лиг для {} записей лидерборда", total);
    }
}
