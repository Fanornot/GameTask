package chocolate.gametask.config;

import chocolate.gametask.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final LeaderboardService leaderboardService;

    // Каждое 1-е число месяца в 03:00 — пересчёт лидерборда и лиг
    @Scheduled(cron = "0 0 3 1 * ?")
    public void recalculateMonthlyLeaderboard() {
        log.info("Запущен ежемесячный пересчёт лидерборда");
        leaderboardService.calculateMonthlyLeaderboard();
    }

    // Каждый день в 00:05 — сброс daily claims
    @Scheduled(cron = "0 5 0 * * ?")
    public void resetDailyFlags() {
        log.info("Сброс ежедневных флагов");
        // Логика сброса реализуется в DailyStreakService
    }
}
