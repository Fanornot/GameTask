package chocolate.gametask.repository;


import chocolate.gametask.entity.Quest;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {
    List<UserQuest> findByUserAndCompletedFalse(User user);
    List<UserQuest> findByUserAndClaimedFalse(User user);
    Optional<UserQuest> findByUserAndQuestAndAssignedDate(User user, Quest quest, LocalDate date);
    List<UserQuest> findByUserAndAssignedDate(User user, LocalDate date);
}