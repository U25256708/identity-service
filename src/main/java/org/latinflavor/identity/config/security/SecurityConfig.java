package org.latinflavor.identity.config.security;

import org.latinflavor.identity.adapter.external.jwt.JwtAuthenticationFilter;
import org.latinflavor.identity.config.properties.WebApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.latinflavor.identity.config.properties.WebApplicationProperties.WebSecurityProperties.PathAuthorizationRule;

import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final WebApplicationProperties props;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ServerResponseAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> setupRequestAuthorization(auth, props.getSecurity()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(this::configureHeaders)
                .build();

    }

    private void configureHeaders(HeadersConfigurer<HttpSecurity> headers) {
        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .contentTypeOptions(Customizer.withDefaults());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", props.getCors().toCorsConfiguration());
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(java.util.List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private void setupRequestAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry httpConfigurer,
                                           WebApplicationProperties.WebSecurityProperties webSecurityProperties) {

        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorization = httpConfigurer
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(webSecurityProperties.unauthenticatedMatchers()).permitAll();

        for (PathAuthorizationRule rule: webSecurityProperties.getAuthorizationRules()) {
            authorization = authorization
                    .requestMatchers(webSecurityProperties.matcherFor(rule.getPaths()))
                    .access(authorityManager(rule));
        }
        authorization
                .requestMatchers(webSecurityProperties.authenticatedMatchers()).authenticated()
                .anyRequest().denyAll();
    }

    private AuthorizationManager<RequestAuthorizationContext> authorityManager(
            WebApplicationProperties.WebSecurityProperties.PathAuthorizationRule rule) {
        return (authentication, context) -> {
            var authorities = authentication.get().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            boolean hasAllowedRole = rule.getRoles().isEmpty()
                    || rule.getRoles().stream().anyMatch(authorities::contains);
            boolean hasRequiredPermissions = authorities.containsAll(rule.getPermissions());
            return new AuthorizationDecision(hasAllowedRole && hasRequiredPermissions);
        };
    }

}
