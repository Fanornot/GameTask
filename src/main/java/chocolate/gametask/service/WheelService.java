package chocolate.gametask.service;


import chocolate.gametask.config.AppProperties;
import chocolate.gametask.dto.WheelSpinResultDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.WheelSpin;
import chocolate.gametask.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.WheelSpinRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WheelService {

    private final WheelSpinRepository wheelSpinRepository;
    private final BonusService bonusService;
    private final AppProperties appProperties;

    private final Random random = new Random();

    // Прозрачные вероятности (сумма = 1.0)
    private static final int[] REWARDS = {10, 20, 30, 50, 100, 200, 500, 1000};
    private static final double[] PROBABILITIES = {0.35, 0.25, 0.15, 0.10, 0.08, 0.04, 0.02, 0.01};

    public Map<String, Object> getProbabilities() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < REWARDS.length; i++) {
            result.put(REWARDS[i] + " бонусов", String.format("%.1f%%", PROBABILITIES[i] * 100));
        }
        result.put("_info", "Вероятности фиксированы и проверяемы. Мат. ожидание ≈ 40 бонусов.");
        return result;
    }

    @Transactional
    public WheelSpinResultDTO spin(User user, boolean useFreeSpin) {
        if (useFreeSpin) {
            // 1 бесплатный спин в неделю
            LocalDateTime weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .atStartOfDay();
            long freeSpins = wheelSpinRepository.countFreeSpinsSince(user, weekStart);
            if (freeSpins >= appProperties.getWheel().getFreeSpinPerWeek()) {
                throw new BusinessLogicException("Бесплатный спин на этой неделе уже использован");
            }
        } else {
            // До 5 платных спинов в день
            LocalDateTime dayStart = LocalDate.now().atTime(LocalTime.MIN);
            long paidSpins = wheelSpinRepository.countPaidSpinsSince(user, dayStart);
            if (paidSpins >= appProperties.getWheel().getPaidSpinPerDay()) {
                throw new BusinessLogicException("Достигнут дневной лимит платных спинов (5 в день)");
            }
            bonusService.debitBonus(user, appProperties.getWheel().getSpinCost(), "WHEEL_SPIN",
                    "Платная прокрутка колеса");
        }

        int reward = calculateReward();
        String rewardType = "BONUS";

        WheelSpin spin = WheelSpin.builder()
                .user(user)
                .isFree(useFreeSpin)
                .rewardAmount(reward)
                .rewardType(rewardType)
                .rewardDescription("Бонус " + reward)
                .build();
        wheelSpinRepository.save(spin);

        bonusService.creditBonus(user, reward, "WHEEL_REWARD", "Награда колеса фортуны");

        return WheelSpinResultDTO.builder()
                .rewardAmount(reward)
                .rewardType(rewardType)
                .rewardDescription("Вы выиграли " + reward + " бонусов!")
                .build();
    }

    private int calculateReward() {
        double rnd = random.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < PROBABILITIES.length; i++) {
            cumulative += PROBABILITIES[i];
            if (rnd <= cumulative) {
                return REWARDS[i];
            }
        }
        return REWARDS[0];
    }
}
