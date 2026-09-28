package org.latinflavor.identity.config;

import org.latinflavor.identity.adapter.external.customer.CustomerServiceClient;
import org.latinflavor.identity.config.properties.ExternalServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ExternalServiceClientConfig {

    @Bean
    CustomerServiceClient customerServiceClient(
            RestClient.Builder builder,
            ExternalServiceProperties properties
    ) {
        return createClient(builder, properties.getCustomer().getBaseUrl(), CustomerServiceClient.class);
    }


    private <T> T createClient(RestClient.Builder builder, String baseUrl, Class<T> clientType) {
        RestClient restClient = builder.clone()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    String authorization = currentAuthorizationHeader();
                    if (authorization != null) {
                        request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorization);
                    }
                    return execution.execute(request, body);
                })
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(clientType);
    }

    private String currentAuthorizationHeader() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }
}
