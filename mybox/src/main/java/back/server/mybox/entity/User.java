package back.server.mybox.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; //pk
    @Column(length = 32, nullable = false)
    private String username; //아이디
    @Column(nullable = false)
    private String password;
    private String nickname;

}
