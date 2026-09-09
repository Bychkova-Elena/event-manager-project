package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.security.jwt.JwtFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private JwtFilterChain jwtFilterChain;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true).ignoring()
                .requestMatchers("/css/**",
                        "/js/**",
                        "/img/**",
                        "/lib/**",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/swagger-config",
                        "/event-manager-openapi.yaml"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users/auth").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/{userId}").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/locations").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/locations").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/locations/{locationId}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/locations/{locationId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/locations/{locationId}").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/events").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/events/my").hasAuthority("USER")
                        .requestMatchers(HttpMethod.DELETE, "/events/{eventId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/events/{eventId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/events/{eventId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/events/search").hasAnyAuthority("ADMIN", "USER")

                        .requestMatchers(HttpMethod.POST, "/events/registrations/{eventId}").hasAuthority("USER")
                        .requestMatchers(HttpMethod.DELETE, "/events/registrations/cancel/{eventId}").hasAuthority("USER")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilterChain, AnonymousAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
