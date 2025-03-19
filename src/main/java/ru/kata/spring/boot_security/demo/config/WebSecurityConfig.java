package ru.kata.spring.boot_security.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final SuccessUserHandler successUserHandler;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests (auth -> auth
                        .requestMatchers ("/admin/**").hasRole("ADMIN")
                        .requestMatchers ("/user/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest ().authenticated ()
                )
                .formLogin (form -> form
                        .successHandler (successUserHandler)
                        .permitAll ()
                )
                .logout (logout -> logout
                        .logoutUrl ("/logout")         // URL выхода
                        .logoutSuccessUrl ("/")        // Куда перенаправлять после выхода
                        .invalidateHttpSession (true)  // Инвалидируем сессию
                        .deleteCookies ("JSESSIONID")  // Удаляем куки сессии
                        .permitAll ());

        return http.build ();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Используем AuthenticationManagerBuilder для настройки аутентификации
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject (AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService (userDetailsService)
                .passwordEncoder (passwordEncoder);
        return authenticationManagerBuilder.build ();
    }

}
