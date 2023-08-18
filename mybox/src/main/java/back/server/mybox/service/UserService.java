package back.server.mybox.service;

import back.server.mybox.entity.User;
import back.server.mybox.repository.UserReposiroty;
import back.server.mybox.dto.UserRequestDto;
import back.server.mybox.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReposiroty userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponseDto join(UserRequestDto requestDto) {
        User user = User.builder()
                .username(requestDto.getUsername())
                .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .build();
        userRepository.save(user);
        UserResponseDto responseDto = new UserResponseDto(user);
        return responseDto;
    }


    public UserResponseDto userInfo(Long userId) {
        User entity = userRepository.findAllByUserId(userId);
        UserResponseDto responseDto = new UserResponseDto(entity);
        return responseDto;
    }
}
