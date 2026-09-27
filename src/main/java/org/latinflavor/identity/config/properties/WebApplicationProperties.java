package org.latinflavor.identity.config.properties;

import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "web")
public class WebApplicationProperties {

    private CorsProperties cors = new CorsProperties();
    private WebSecurityProperties security = new WebSecurityProperties();

    @Data
    public static class CorsProperties {

        private Long maxAge = 3600L;
        private boolean allowCredentials = true;
        private List<String> allowedHeaders = new ArrayList<>();
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> exposedHeaders = new ArrayList<>();

        public CorsConfiguration toCorsConfiguration() {

            CorsConfiguration config = new CorsConfiguration();

            config.setMaxAge(maxAge);
            config.setAllowCredentials(allowCredentials);
            config.setAllowedHeaders(allowedHeaders);
            config.setAllowedOrigins(allowedOrigins);
            config.setAllowedMethods(allowedMethods);
            config.setExposedHeaders(exposedHeaders);
            return config;
        }
    }

    @Data
    public static class WebSecurityProperties {

        private List<String> unauthenticatedPaths = new ArrayList<>();

        private List<PathAuthorizationRule> authorizationRules = new ArrayList<>();

        public RequestMatcher matcherFor(List<String> paths) {
            return buildMatcher(paths);
        }

        public RequestMatcher unauthenticatedMatchers() {
            return buildMatcher(unauthenticatedPaths);
        }

        private RequestMatcher buildMatcher(List<String> paths) {
            List<RequestMatcher> matchers = createPathMatchers(paths);
            if (matchers.isEmpty()) return request -> false;
            return new OrRequestMatcher(matchers);
        }

        private List<RequestMatcher> createPathMatchers(List<String> paths) {
            return paths.stream()
                    .map(this::toRequestMatcher)
                    .toList();
        }

        private RequestMatcher toRequestMatcher(String path) {
            return PathPatternRequestMatcher
                    .withDefaults()
                    .matcher(path);
        }

        @Data
        public static class PathAuthorizationRule {
            private String path;
            private HttpMethod method;
            private List<String> roles = new ArrayList<>();
            private Set<String> permissions = new HashSet<>();
        }
    }
}
