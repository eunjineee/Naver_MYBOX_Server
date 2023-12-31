package back.server.mybox.common;


import back.server.mybox.jwt.JwtAuthenticationFilter;
import back.server.mybox.jwt.JwtAuthorizationFilter;
import back.server.mybox.jwt.service.JwtService;
import back.server.mybox.Domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CorsConfig config;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Autowired
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .antMatchers("/user/join","**/login","/refresh/**")
                .antMatchers("**/refresh/**","/api/user/auth/refresh/**","/user/auth/refresh/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다!(login시 세션을 검증하는 필터를 사용하지 않겠다)
                .and()
                .formLogin().disable() //formLogin(form)방식 사용 안함 , json방식으로 전달
                .httpBasic().disable() //Bearer 방식 사용 -> header 에 authentication 에 토큰을 넣어 전달하는 방식
                .addFilter(config.corsFilter())
                .apply(new MyCustomDsl())
                .and()

                .authorizeRequests()
                .antMatchers("**").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .and()
                .build()
                ;
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

            http
                .addFilter(config.corsFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtService)) //AuthenticationManger가 있어야 된다.(파라미터로)
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, jwtService));
        }
    }
}
