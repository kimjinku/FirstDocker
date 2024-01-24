package com.korea.project2_team4.Config;

import com.korea.project2_team4.Service.PrincipalOauth2UserService;
import lombok.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Builder
public class SecurityConfig {

    private static final String SOCIAL_LOGIN = "social_login";

    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/sendVerificationCode")).permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/chat/**"))
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .formLogin((formLogin) -> formLogin
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/")
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/member/login?error=true");
                        }))
                .oauth2Login((oauth2Login) -> oauth2Login
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/member/signup/social")
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig.userService(principalOauth2UserService))
                )
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))


        ;
        return http.build();
    }




}
