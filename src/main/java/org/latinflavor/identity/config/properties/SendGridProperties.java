package org.latinflavor.identity.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "external.sendgrid")
public class SendGridProperties {

    private boolean enabled;
    private String apiKey;
    private String fromEmail;
    private String templateId;
}
