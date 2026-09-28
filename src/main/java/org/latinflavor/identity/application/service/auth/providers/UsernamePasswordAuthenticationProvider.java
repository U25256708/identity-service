package org.latinflavor.identity.application.service.auth.providers;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.external.jwt.IssuedToken;
import org.latinflavor.identity.application.port.out.external.jwt.TokenPort;
import org.latinflavor.identity.application.port.out.external.jwt.TokenSubject;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.latinflavor.identity.domain.errors.UserErrors.INVALID_CREDENTIALS;

@Service
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserSearchPort userSearchPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenPort tokenPort;

    @Override
    @Transactional(readOnly = true)
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getPrincipal().toString();
        String password = token.getCredentials().toString();
        User user = retrievalUser(username, password);

        TokenSubject subject = new TokenSubject(user.getId(), user.getEmail(),
                user.getRoleNames(), user.getPermissionCodes(), user.getTokenVersion());
        IssuedToken issuedToken = tokenPort.generate(subject);
        List<SimpleGrantedAuthority> authorities = Stream.concat(subject.roles().stream(),
                        subject.permissions().stream())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(issuedToken, null, authorities);
    }

    private User retrievalUser(String username, String password) {
        return userSearchPort.findByUsername(username)
                .filter(candidate -> TRUE.equals(candidate.getActive()))
                .filter(candidate -> candidate.getPasswordHash() != null
                        && passwordEncoder.matches(password, candidate.getPasswordHash()))
                .orElseThrow(() -> new ApplicationException(INVALID_CREDENTIALS));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
