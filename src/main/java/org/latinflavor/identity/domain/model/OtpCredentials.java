package org.latinflavor.identity.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.latinflavor.identity.shared.persistence.AbstractAuditable;

import java.time.Instant;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;

@Entity
@Table(name = "otp_credentials", schema = "identity_schema")
@Getter
@Setter
@NoArgsConstructor
public class OtpCredentials extends AbstractAuditable<UUID> {

    private String email;
    private String codeHash;
    private Integer attempts;
    private Instant validUntil;
    private Instant validatedAt;
    private Boolean isValidated;


    private OtpCredentials(String email, String codeHash) {
        this.email = email;
        this.codeHash = codeHash;
        this.attempts = 0;
        this.validUntil = Instant.now().plus(8, MINUTES);
        this.isValidated = false;
    }

    public static OtpCredentials of(String email, String codeHash) {
        return new OtpCredentials(email, codeHash);
    }

    public boolean isExpired() {
        return validUntil.isBefore(Instant.now());
    }

    public void registerFailedAttempt() {
        attempts++;
    }

    public void markAsValidated() {
        isValidated = true;
        validatedAt = Instant.now();
        codeHash = null;
    }

}
