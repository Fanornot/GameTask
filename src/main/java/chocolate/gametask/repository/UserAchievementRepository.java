package chocolate.gametask.repository;
import chocolate.gametask.entity.Achievement;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUser(User user);
    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement);
    boolean existsByUserAndAchievement(User user, Achievement achievement);
}
