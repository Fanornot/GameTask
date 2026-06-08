package chocolate.gametask.service;

import chocolate.gametask.dto.CreateQuestRequest;
import chocolate.gametask.dto.QuestDTO;
import chocolate.gametask.entity.Quest;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.UserQuest;
import chocolate.gametask.exception.BusinessLogicException;
import chocolate.gametask.exception.ResourceNotFoundException;
import chocolate.gametask.repository.QuestRepository;
import chocolate.gametask.repository.UserQuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final BonusService bonusService;

    @Transactional
    public QuestDTO createQuest(CreateQuestRequest request) {
        Quest quest = Quest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .targetCount(request.getTargetCount())
                .rewardAmount(request.getRewardAmount())
                .targetAudience(request.getTargetAudience() != null ? request.getTargetAudience() : "ALL")
                .active(true)
                .build();
        quest = questRepository.save(quest);
        return toDTO(quest, 0, false, false);
    }

    @Transactional(readOnly = true)
    public List<QuestDTO> getActiveQuests(User user, String type) {
        List<Quest> quests = (type != null)
                ? questRepository.findByActiveTrueAndType(type)
                : questRepository.findByActiveTrue();

        LocalDate today = LocalDate.now();
        return quests.stream().map(q -> {
            UserQuest uq = userQuestRepository.findByUserAndQuestAndAssignedDate(user, q, today).orElse(null);
            return toDTO(q,
                    uq != null ? uq.getProgress() : 0,
                    uq != null && Boolean.TRUE.equals(uq.getCompleted()),
                    uq != null && Boolean.TRUE.equals(uq.getClaimed()));
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateProgress(User user, Long questId, int increment) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Квест не найден"));

        UserQuest uq = userQuestRepository.findByUserAndQuestAndAssignedDate(user, quest, LocalDate.now())
                .orElseGet(() -> UserQuest.builder()
                        .user(user).quest(quest)
                        .assignedDate(LocalDate.now())
                        .progress(0).completed(false).claimed(false)
                        .build());

        if (!Boolean.TRUE.equals(uq.getCompleted())) {
            uq.setProgress(uq.getProgress() + increment);
            if (uq.getProgress() >= quest.getTargetCount()) {
                uq.setCompleted(true);
                uq.setCompletedAt(LocalDateTime.now());
            }
            userQuestRepository.save(uq);
        }
    }

    @Transactional
    public Integer claimReward(User user, Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Квест не найден"));

        UserQuest uq = userQuestRepository.findByUserAndQuestAndAssignedDate(user, quest, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("Квест не назначен пользователю"));

        if (!Boolean.TRUE.equals(uq.getCompleted())) {
            throw new BusinessLogicException("Квест ещё не выполнен");
        }
        if (Boolean.TRUE.equals(uq.getClaimed())) {
            throw new BusinessLogicException("Награда уже получена");
        }

        uq.setClaimed(true);
        userQuestRepository.save(uq);
        bonusService.creditBonus(user, quest.getRewardAmount(), "QUEST",
                "Награда за квест: " + quest.getName());
        return quest.getRewardAmount();
    }

    private QuestDTO toDTO(Quest q, int progress, boolean completed, boolean claimed) {
        return QuestDTO.builder()
                .id(q.getId())
                .name(q.getName())
                .description(q.getDescription())
                .type(q.getType())
                .targetCount(q.getTargetCount())
                .rewardAmount(q.getRewardAmount())
                .progress(progress)
                .completed(completed)
                .claimed(claimed)
                .build();
    }
}