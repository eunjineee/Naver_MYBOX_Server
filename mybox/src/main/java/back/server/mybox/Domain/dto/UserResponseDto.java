package back.server.mybox.Domain.dto;

import back.server.mybox.Domain.entity.UserEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserResponseDto {
    private String username;
    private String nickname;
    private Long privateFolder;

    public UserResponseDto(UserEntity e){
        this.username = e.getUsername();
        this.nickname = e.getNickname();
        this.privateFolder = e.getPrivateFolder();
    }
}
