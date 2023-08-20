package back.server.mybox.Domain.entity;


import back.server.mybox.jwt.entity.RefreshToken;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; //pk
    @Column(length = 32, nullable = false)
    private String username; //아이디
    @Column(nullable = false)
    private String password;
    private String nickname;
    private Long privateFolder;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "refreshTokenId")
    private RefreshToken jwtRefreshToken;

    public void createRefreshToken(RefreshToken refreshToken) {
        this.jwtRefreshToken = refreshToken;
    }
    public void SetRefreshToken(String refreshToken) {
        this.jwtRefreshToken.setRefreshToken(refreshToken);
    }
    public void SetPrivateFolder(Long folderId) {
        this.privateFolder = folderId;
    }
    public void logout(){
        this.jwtRefreshToken = null;
    }
}
