package chocolate.gametask.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserDataJpa extends JpaRepository<User,Long> {

}
