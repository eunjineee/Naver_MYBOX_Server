package back.server.mybox.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReposiroty extends JpaRepository<User, Long> {
    User findAllByUserId(Long userId);
}
