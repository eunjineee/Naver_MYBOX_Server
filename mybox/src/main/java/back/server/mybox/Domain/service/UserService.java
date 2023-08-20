package back.server.mybox.Domain.service;

import back.server.mybox.Domain.entity.UserEntity;
import back.server.mybox.jwt.entity.RefreshToken;
import back.server.mybox.jwt.repository.RefreshTokenRepository;
import back.server.mybox.Domain.repository.UserRepository;
import back.server.mybox.Domain.dto.UserRequestDto;
import back.server.mybox.Domain.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NcpService ncpService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponseDto join(UserRequestDto requestDto) {
        UserEntity userEntity = UserEntity.builder()
                .username(requestDto.getUsername())
                .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .build();
        userRepository.save(userEntity);
        userEntity.SetPrivateFolder(ncpService.createPrivateFolder(userEntity.getUserId(), userEntity.getUsername()));
        UserResponseDto responseDto = new UserResponseDto(userEntity);
        return responseDto;
    }

    public UserResponseDto userInfo(Long userId) {
        UserEntity entity = userRepository.findAllByUserId(userId);
        UserResponseDto responseDto = new UserResponseDto(entity);
        return responseDto;
    }

    public void userLogout(Long userId){
        UserEntity userEntity = userRepository.findByUserId(userId);
        RefreshToken token = refreshTokenRepository.findRefreshTokenById(userEntity.getJwtRefreshToken().getId());
        refreshTokenRepository.delete(token);
        userEntity.logout();
    }
}
