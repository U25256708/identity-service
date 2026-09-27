package org.latinflavor.identity.application.service.otp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.latinflavor.identity.application.port.in.otp.GenerateOtpUseCase;
import org.latinflavor.identity.application.port.out.external.NotificationPort;
import org.latinflavor.identity.application.port.out.otp.OtpCredentialsPersistPort;
import org.latinflavor.identity.domain.model.OtpCredentials;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;


@Service
@RequiredArgsConstructor
public class GenerateCodeUseCaseImpl implements GenerateOtpUseCase {

    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final OtpCredentialsPersistPort persistPort;
    private final NotificationPort notificationPort;

    @Override
    public void generateCode(String email) {
        String code = generateVerificationCode();
        String codeHashed = DigestUtils.sha256Hex(email + code);
        persistPort.persist(OtpCredentials.of(email, codeHashed));
        notificationPort.notify(email, code);
    }

    public String generateVerificationCode() {
        int code = RANDOM.nextInt((int) Math.pow(10, CODE_LENGTH));
        return String.format("%0" + CODE_LENGTH + "d", code);
    }

}
