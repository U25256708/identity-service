package org.latinflavor.identity.application.port.out.otp;

import org.latinflavor.identity.domain.model.OtpCredentials;

public interface OtpCredentialsPersistPort {

    void persist(OtpCredentials entity);
}
