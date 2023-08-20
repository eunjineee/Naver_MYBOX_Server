package back.server.mybox.Domain.repository;

import back.server.mybox.Domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findAllByUserId(Long userId);
    User findByUsername(String username);
    User findByUserId(Long userId);
}
