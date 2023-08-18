package back.server.mybox.repository;

import back.server.mybox.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReposiroty extends JpaRepository<User, Long> {
    User findAllByUserId(Long userId);
}
