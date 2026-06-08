package chocolate.gametask.repository;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.UserPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserPrizeRepository extends JpaRepository<UserPrize, Long> {
    List<UserPrize> findByUserOrderByPurchasedAtDesc(User user);
}