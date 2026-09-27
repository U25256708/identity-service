package org.latinflavor.identity.application.port.out.otp;

import org.latinflavor.identity.domain.model.OtpCredentials;

import java.util.Optional;

public interface OtpCredentialsSearchPort {

    Optional<OtpCredentials> findLatestPendingByEmail(String email);
}
