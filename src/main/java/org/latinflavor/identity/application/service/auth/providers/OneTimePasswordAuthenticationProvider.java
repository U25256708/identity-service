package org.latinflavor.identity.application.service.auth.providers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.latinflavor.identity.application.service.auth.OneTimePasswordAuthenticationToken;
import org.latinflavor.identity.application.service.auth.AuthorizationCatalog;
import org.latinflavor.identity.application.port.out.external.IssuedToken;
import org.latinflavor.identity.application.port.out.external.TokenPort;
import org.latinflavor.identity.application.port.out.external.TokenSubject;
import org.latinflavor.identity.application.port.out.otp.OtpCredentialsPersistPort;
import org.latinflavor.identity.application.port.out.otp.OtpCredentialsSearchPort;
import org.latinflavor.identity.application.port.out.user.UserPersistPort;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.*;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.latinflavor.identity.domain.errors.OtpErrors.EXPIRED_OTP_CODE;
import static org.latinflavor.identity.domain.errors.OtpErrors.INVALID_OTP_CODE;
import static org.latinflavor.identity.domain.errors.UserErrors.USER_IS_NOT_ACTIVE;
import static org.latinflavor.identity.domain.model.PermissionCode.BASIC_MANAGEMENT;
import static org.latinflavor.identity.domain.model.RoleName.BASIC;

@Service
@RequiredArgsConstructor
public class OneTimePasswordAuthenticationProvider implements AuthenticationProvider {

    private final OtpCredentialsSearchPort otpCredentialsSearchPort;
    private final OtpCredentialsPersistPort otpCredentialsPersistPort;
    private final UserSearchPort userSearchPort;
    private final UserPersistPort userPersistPort;
    private final AuthorizationCatalog authorizationCatalog;
    private final TokenPort tokenPort;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OneTimePasswordAuthenticationToken token = (OneTimePasswordAuthenticationToken) authentication;
        String email = token.getPrincipal().toString();
        String code = token.getCredentials().toString();
        OtpCredentials otpCredentials = checkOtp(email, code);

        TokenSubject subject = createTokenSubject(email);
        IssuedToken issuedToken = tokenPort.generate(subject);
        markAsValidated(otpCredentials);

        List<SimpleGrantedAuthority> authorities = Stream.concat(subject.roles().stream(), subject.permissions().stream())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();

        OneTimePasswordAuthenticationToken authenticatedToken = new OneTimePasswordAuthenticationToken(issuedToken, authorities);
        authenticatedToken.setDetails(issuedToken);
        return authenticatedToken;
    }

    private TokenSubject createTokenSubject(String email) {
        User user = retrieveUser(email);
        return new TokenSubject(user.getId(), user.getEmail(), user.getRoleNames(),
                user.getPermissionCodes(), user.getTokenVersion());
    }

    private User retrieveUser(String email) {
        User user = userSearchPort.findByEmail(email)
                .orElseGet(() -> createUserIfNotExists(email));
        if (!TRUE.equals(user.getActive())) {
            throw new ApplicationException(USER_IS_NOT_ACTIVE, email);
        }
        return user;
    }

    private User createUserIfNotExists(String email) {
        return userPersistPort.persist(User.ofCustomer(email)
                .assignRole(authorizationCatalog.requireActiveRole(BASIC))
                .applyPermissions(Set.of(authorizationCatalog.requireActivePermission(BASIC_MANAGEMENT))));
    }

    private OtpCredentials checkOtp(String email, String code) {
        OtpCredentials otpCredentials = otpCredentialsSearchPort.findLatestPendingByEmail(email)
                .orElseThrow(() -> new ApplicationException(INVALID_OTP_CODE));

        if (otpCredentials.isExpired()) {
            throw new ApplicationException(EXPIRED_OTP_CODE);
        }

        if (otpCredentials.getAttempts() >= 3) {
            throw new ApplicationException(INVALID_OTP_CODE);
        }

        boolean codeMatches = MessageDigest.isEqual(
                otpCredentials.getCodeHash().getBytes(),
                DigestUtils.sha256Hex(email + code).getBytes());

        if (!codeMatches) {
            otpCredentials.registerFailedAttempt();
            otpCredentialsPersistPort.persist(otpCredentials);
            throw new ApplicationException(INVALID_OTP_CODE);
        }

        return otpCredentials;
    }

    private void markAsValidated(OtpCredentials otpCredentials) {
        otpCredentials.markAsValidated();
        otpCredentialsPersistPort.persist(otpCredentials);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OneTimePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
