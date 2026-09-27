package org.latinflavor.identity.adapter.persistence.otp;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.otp.OtpCredentialsPersistPort;
import org.latinflavor.identity.application.port.out.otp.OtpCredentialsSearchPort;
import org.latinflavor.identity.domain.model.OtpCredentials;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OtpCredentialsRepositoryAdapter implements OtpCredentialsPersistPort, OtpCredentialsSearchPort {

    private final OtpCredentialsRepository repository;

    @Override
    public void persist(OtpCredentials entity) {
        repository.save(entity);
    }

    @Override
    public Optional<OtpCredentials> findLatestPendingByEmail(String email) {
        return repository.findFirstByEmailIgnoreCaseAndIsValidatedFalseOrderByCreatedAtDesc(email);
    }
}
