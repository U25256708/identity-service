package org.latinflavor.identity.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String issuer;
    private String audience;
    private String keyId;
    private Duration accessTokenTtl = Duration.ofMinutes(15);
    private String privateKeyBase64;
    private String publicKeyBase64;
}
