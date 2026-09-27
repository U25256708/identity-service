package org.latinflavor.identity.adapter.external.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.external.TokenPort;
import org.latinflavor.identity.application.port.out.external.TokenSubject;
import org.springframework.http.HttpHeaders;
import org.latinflavor.identity.config.security.ServerResponseAuthenticationEntryPoint;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenPort tokenPort;
    private final ServerResponseAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractBearerToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        TokenSubject subject = validateToken(token, request, response);
        if (subject == null) return;
        setAuthentication(subject);
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    private TokenSubject validateToken(String token, HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        try {
            return tokenPort.validate(token);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            SecurityContextHolder.clearContext();
            System.out.println("a");
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid or expired JWT", exception));
            return null;
        }

    }

    private void setAuthentication(TokenSubject subject) {
        List<SimpleGrantedAuthority> authorities = Stream.concat(
                        subject.roles().stream(), subject.permissions().stream())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
