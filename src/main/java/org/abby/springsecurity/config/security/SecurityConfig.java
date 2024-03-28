package org.abby.springsecurity.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: SecurityConfig
 * <p>
 * 目的:負責整個APP的所有http請求的安全性
 * <p>
 * 1.在Springboot3.0以上,這兩個@Configuration & @EnableWebSecurity 要一起配
 * <p>
 * <p>
 * 2.!!! IMPORTANT !!! WHITELIST 跟 JwtAuthenticationAndOncePerRequestFilter 會互相影響
 * <p>
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationAndOncePerRequestFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    protected static final String[] SWAGGER_WHITELIST = {
            "/swagger-resources/**",
            "/webjars/**",
            "/swagger-ui/**",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs/swagger-config"
    };
    protected static final String[] API_POST_WHITELIST = {
            "/user/login",
    };

    @Value("${cors.allowed.origin}")
    private String allowedOrigin;

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    //todo 依照環境區分cors放行
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity
    ) throws Exception {

        httpSecurity
                /**
                 * 支援跨域請求
                 */
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                /**
                 * 防止csrf攻擊
                 */
                .csrf(AbstractHttpConfigurer::disable)
                /**
                 * 禁止用Session
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /**
                 * 白名單
                 */
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(SWAGGER_WHITELIST[0]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[1]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[2]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[3]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[4]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[5]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[6]).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST[7]).permitAll()
                                .requestMatchers("/static/**").permitAll()//放行靜態資源
                                .requestMatchers(HttpMethod.POST, API_POST_WHITELIST[0]).permitAll()//放行靜態資源
                                .anyRequest().authenticated()
                );
        httpSecurity.authenticationProvider(authenticationProvider);
        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
