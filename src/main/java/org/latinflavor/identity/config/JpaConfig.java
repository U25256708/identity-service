package org.latinflavor.identity.config;

import org.latinflavor.identity.application.port.out.external.TokenSubject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(
        auditorAwareRef = "auditorProvider",
        dateTimeProviderRef = "auditingDateTimeProvider"
)
@EnableJpaRepositories(basePackages = {
        "org.latinflavor.identity.adapter.persistence.user",
        "org.latinflavor.identity.adapter.persistence.otp",
        "org.latinflavor.identity.adapter.persistence.permission",
        "org.latinflavor.identity.adapter.persistence.role",
}
)
public class JpaConfig {

    @Bean(name = "auditorProvider")
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(TokenSubject.class::isInstance)
                .map(TokenSubject.class::cast)
                .map(subject -> subject.userId().toString())
                .or(() -> Optional.of("SYSTEM"));
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider() {
        ZoneId limaTimeZone = ZoneId.of("America/Lima");
        return () -> Optional.of(LocalDateTime.now(limaTimeZone));
    }
}
