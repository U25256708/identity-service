package org.latinflavor.identity.service.authentication.providers;

import org.latinflavor.identity.shared.exception.ApplicationException;
import org.latinflavor.identity.service.authentication.factory.OneTimePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.latinflavor.identity.domain.OtpErrors.INVALID_OTP_CODE;

@Service
@RequiredArgsConstructor
public class OneTimePasswordAuthenticationProvider implements AuthenticationProvider {


    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OneTimePasswordAuthenticationToken token = (OneTimePasswordAuthenticationToken) authentication;
        String email = token.getPrincipal().toString();
        String code = token.getCredentials().toString();
        validateCodeRequest(code);
        return new OneTimePasswordAuthenticationToken(null, List.of());
    }

    private static void validateCodeRequest(String code) {
        if (code == null || code.isBlank()) {
            throw new ApplicationException(INVALID_OTP_CODE, code);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OneTimePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
