package com.blogwebsite.blogwebapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM bloguser WHERE username = ?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT username, authority FROM bloguser WHERE username = ?");

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests(configure ->
                configure
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/posts").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/posts/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/posts/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/posts/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/posts/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()

        ).formLogin(
                form -> form
                        .loginPage("/login/user-login")
                        .loginProcessingUrl("/authenticateTheUser")
                        .permitAll()
                        .defaultSuccessUrl("/", false)

        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()

        ).exceptionHandling(configure -> configure
                .accessDeniedPage("/access-denied")

        );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
