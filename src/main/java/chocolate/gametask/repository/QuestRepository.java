package chocolate.gametask.repository;
import chocolate.gametask.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByActiveTrueAndType(String type);
    List<Quest> findByActiveTrue();
}