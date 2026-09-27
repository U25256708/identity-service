package org.latinflavor.identity.adapter.persistence.otp;

import org.latinflavor.identity.domain.model.OtpCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCredentialsRepository extends JpaRepository<OtpCredentials, UUID> {

    Optional<OtpCredentials> findFirstByEmailIgnoreCaseAndIsValidatedFalseOrderByCreatedAtDesc(String email);
}
