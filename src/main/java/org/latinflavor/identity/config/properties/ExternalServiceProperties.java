package org.latinflavor.identity.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "external.services")
public class ExternalServiceProperties {

    private Service customer = new Service();

    @Getter
    @Setter
    public static class Service {
        private String baseUrl;
    }
}
