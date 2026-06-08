package chocolate.gametask.service;


import chocolate.gametask.dto.AchievementDTO;
import chocolate.gametask.entity.Achievement;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.UserAchievement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.AchievementRepository;
import chocolate.gametask.repository.UserAchievementRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final BonusService bonusService;

    @Transactional(readOnly = true)
    public List<AchievementDTO> getAllWithStatus(User user) {
        return achievementRepository.findAll().stream()
                .map(a -> AchievementDTO.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .description(a.getDescription())
                        .icon(a.getIcon())
                        .conditionType(a.getConditionType())
                        .threshold(a.getThreshold())
                        .rewardAmount(a.getRewardAmount())
                        .unlocked(userAchievementRepository.existsByUserAndAchievement(user, a))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndUnlock(User user, String conditionType, int currentValue) {
        List<Achievement> achievements = achievementRepository.findAll().stream()
                .filter(a -> a.getConditionType().equals(conditionType))
                .filter(a -> currentValue >= a.getThreshold())
                .collect(Collectors.toList());

        for (Achievement a : achievements) {
            if (!userAchievementRepository.existsByUserAndAchievement(user, a)) {
                UserAchievement ua = UserAchievement.builder()
                        .user(user)
                        .achievement(a)
                        .unlockedAt(LocalDateTime.now())
                        .build();
                userAchievementRepository.save(ua);
                bonusService.creditBonus(user, a.getRewardAmount(), "ACHIEVEMENT",
                        "Достижение: " + a.getName());
                log.info("Разблокировано достижение '{}' для пользователя {}", a.getName(), user.getUsername());
            }
        }
    }
}
