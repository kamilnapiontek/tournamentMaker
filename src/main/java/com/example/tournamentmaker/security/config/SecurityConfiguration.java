package com.example.tournamentmaker.security.config;

import com.example.tournamentmaker.user.PermissionEnum;
import com.example.tournamentmaker.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**")
                .permitAll()
                .requestMatchers("/api/v1/management/**").hasAnyRole(Role.ADMIN.name(), Role.MANAGER.name())
                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(PermissionEnum.ADMIN_READ.name(),
                        PermissionEnum.MANAGER_READ.name())
                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(PermissionEnum.ADMIN_CREATE.name(),
                        PermissionEnum.MANAGER_CREATE.name())
                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(PermissionEnum.ADMIN_UPDATE.name(),
                        PermissionEnum.MANAGER_UPDATE.name())
                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(PermissionEnum.ADMIN_DELETE.name(),
                        PermissionEnum.MANAGER_DELETE.name()).anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
        ;

        return http.build();
    }
}
