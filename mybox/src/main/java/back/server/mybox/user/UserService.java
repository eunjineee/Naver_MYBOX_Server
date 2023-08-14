package back.server.mybox.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReposiroty userRepository;

    public UserResponseDto userInfo(Long userId) {
        User entity = userRepository.findAllByUserId(userId);
        UserResponseDto responseDto = new UserResponseDto(entity);
        return responseDto;
    }
}
