package back.server.mybox.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserResponseDto {
    private String username;
    private String nickname;

    public UserResponseDto(User e){
        this.username = e.getUsername();
        this.nickname = e.getNickname();
    }
}
