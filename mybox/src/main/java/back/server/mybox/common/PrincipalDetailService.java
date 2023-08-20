package back.server.mybox.common;


import back.server.mybox.Domain.entity.UserEntity;
import back.server.mybox.Domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
//http://localhost:8080/login 호출시 (스프링 시큐리티 자동 uri) -> 동직을 하지 않는다. formLogin사용 안하니 => SpringSecuriyFilter를 extends해서 해결
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailService loadUserByUsername 실행중   " + username);
        UserEntity findUserEntity = userRepository.findByUsername(username);
        System.out.println("PrincipalDetailService loadUserByUsername에서 찾은 USER : " + findUserEntity);

        return new PrincipalDetails(findUserEntity);
    }
}
