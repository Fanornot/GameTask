package chocolate.gametask.service;
import chocolate.gametask.config.AppProperties;
import chocolate.gametask.entity.DailyStreak;
import chocolate.gametask.entity.User;
import chocolate.gametask.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.DailyStreakRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DailyStreakService {

    private final DailyStreakRepository dailyStreakRepository;
    private final BonusService bonusService;
    private final AppProperties appProperties;

    @Transactional(readOnly = true)
    public DailyStreak getStreak(User user) {
        return dailyStreakRepository.findByUser(user)
                .orElseGet(() -> createStreak(user));
    }

    @Transactional
    public DailyStreak createStreak(User user) {
        DailyStreak streak = DailyStreak.builder()
                .user(user)
                .currentStreak(0)
                .maxStreak(0)
                .claimedToday(false)
                .freezesAvailable(0)
                .build();
        return dailyStreakRepository.save(streak);
    }

    @Transactional
    public Map<String, Object> claimDailyReward(User user) {
        DailyStreak streak = getStreak(user);
        LocalDate today = LocalDate.now();

        if (Boolean.TRUE.equals(streak.getClaimedToday()) &&
                today.equals(streak.getLastClaimDate())) {
            throw new BusinessLogicException("Награда за сегодня уже получена");
        }

        // Рассчёт стрика
        if (streak.getLastClaimDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(streak.getLastClaimDate(), today);
            if (daysBetween > 1 && streak.getFreezesAvailable() <= 0) {
                streak.setCurrentStreak(0); // Сброс
            } else if (daysBetween > 1 && streak.getFreezesAvailable() > 0) {
                streak.setFreezesAvailable(streak.getFreezesAvailable() - 1); // Использована заморозка
            }
        }

        streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        if (streak.getCurrentStreak() > streak.getMaxStreak()) {
            streak.setMaxStreak(streak.getCurrentStreak());
        }
        streak.setLastClaimDate(today);
        streak.setClaimedToday(true);
        dailyStreakRepository.save(streak);

        // Награда
        int reward = calculateReward(streak.getCurrentStreak());
        bonusService.creditBonus(user, reward, "DAILY", "Ежедневная награда. День " + streak.getCurrentStreak());

        Map<String, Object> result = new HashMap<>();
        result.put("currentStreak", streak.getCurrentStreak());
        result.put("reward", reward);
        result.put("message", "Получено " + reward + " бонусов! Стрик: " + streak.getCurrentStreak() + " дн.");
        return result;
    }

    @Transactional
    public void buyFreeze(User user) {
        int cost = appProperties.getDaily().getFreezeCost();
        bonusService.debitBonus(user, cost, "FREEZE", "Покупка заморозки стрика");
        DailyStreak streak = getStreak(user);
        streak.setFreezesAvailable(streak.getFreezesAvailable() + 1);
        dailyStreakRepository.save(streak);
    }

    private int calculateReward(int streakDays) {
        if (streakDays % 30 == 0) return 500;
        if (streakDays % 14 == 0) return 150;
        if (streakDays % 7 == 0) return 50;
        return appProperties.getDaily().getBaseReward();
    }
}