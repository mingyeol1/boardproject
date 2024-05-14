package org.zerock.b01.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomSocialLoginSuccessHandler;
import org.zerock.b01.security.handler.Custom403Handler;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity //해당 메서드에만 인가 처리를 하겠다. @PreAuthorize("hasRole('USER')") ->boardController
public class CustomSecurityConfig {

    //데이터 소스 생성
    // Remember-me 서비스를 위해서 DataSource와 CustomUserDetailsService
    private final DataSource dataSource;

    private final UserDetailsService UserDetailsService; //사용자와 관련된 정보 불러오기 .


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("---------------------------configure-------------------------------");

        //사용자 로그인 form 페이지 설정
        http.formLogin(form ->{
            form.loginPage("/member/login");
        });

        //csrf설정
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        // remember-me설정
        http.rememberMe(httpSecurityRememberMeConfigurer -> {
           httpSecurityRememberMeConfigurer.key("12345678")
                   .tokenRepository(persistentTokenRepository())
                   .userDetailsService(UserDetailsService)      //PasswordEncoder 에 의한 순환 구조가 발생할 수 있다.
                   .tokenValiditySeconds(60 * 60 * 24 * 30);
        });

        //exceptionHandler 설정
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
        });

        http.oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
           httpSecurityOAuth2LoginConfigurer.loginPage("/member/login");
           httpSecurityOAuth2LoginConfigurer.successHandler(authenticationSuccessHandler()); //로그인하고 로그인 처리후 로그인이 성공하면 authenticationSuccessHandler가 동작함.

        });

        return http.build();
    }

    private final PasswordEncoder passwordEncoder; //순환 참조로 인해서 passwordEncoder 삭제 후 ...
    //PasswordEncoderConfig 에 있는 passwordEncoder 를 불러옴.

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder);

    }

    // AccessDeniedHandler 빈 등록.
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("-----------------------------    Web configure   -----------------------------");
    //정적 리소스 필터링 제외 static폴더 제외 모든 동적 즉 templates 폴더는 모두 로그인(인증)을 해야함
        return (web -> web.ignoring().requestMatchers(PathRequest.toStaticResources()
            .atCommonLocations()));
    }

    // 패스워드 암호화 처리하는 객체 -- 순환참조 문제로 인해서 다른 별개의 Configureation을 생성하여 처리
    // 순환 구조의 발생 원인은 userDetailsService에서 의존성 주입을 한 passwordEncoder를 설정한 Configuration에서
    // 다시 불러오는 구조가 되어 순환 구조가 된다.
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    //PersistentTokenRepository
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

}
