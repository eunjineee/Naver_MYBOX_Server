package back.server.mybox.Domain.repository;

import back.server.mybox.Domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findAllByUserId(Long userId);
    UserEntity findByUsername(String username);
    UserEntity findByUserId(Long userId);
}
