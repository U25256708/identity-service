package org.latinflavor.identity.adapter.external.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.latinflavor.identity.application.port.out.external.IssuedToken;
import org.latinflavor.identity.application.port.out.external.TokenPort;
import org.latinflavor.identity.application.port.out.external.TokenSubject;
import org.latinflavor.identity.config.properties.JwtProperties;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenAdapter implements TokenPort {

    private final JwtProperties properties;

    public JwtTokenAdapter(JwtProperties properties) {
        this.properties = properties;
    }

    @Override
    public IssuedToken generate(TokenSubject subject) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.getAccessTokenTtl());

        String token = Jwts.builder()
                .header().keyId(properties.getKeyId()).and()
                .issuer(properties.getIssuer())
                .subject(subject.userId().toString())
                .audience().add(properties.getAudience()).and()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .id(UUID.randomUUID().toString())
                .claim("email", subject.email())
                .claim("roles", subject.roles())
                .claim("permissions", subject.permissions())
                .claim("ver", subject.tokenVersion())
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();

        return new IssuedToken(token, properties.getAccessTokenTtl().toSeconds(), subject.permissions());
    }

    @Override
    public TokenSubject validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey())
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!claims.getAudience().contains(properties.getAudience())) {
            throw new IllegalArgumentException("JWT audience is invalid");
        }

        List<?> roles = claims.get("roles", List.class);
        Set<String> roleNames = roles == null ? Set.of() : roles.stream()
                .map(Object::toString)
                .collect(Collectors.toUnmodifiableSet());

        List<?> permissions = claims.get("permissions", List.class);
        Set<String> permissionCodes = permissions == null ? Set.of() : permissions.stream()
                .map(Object::toString)
                .collect(Collectors.toUnmodifiableSet());

        Number tokenVersion = claims.get("ver", Number.class);
        return new TokenSubject(
                UUID.fromString(claims.getSubject()),
                claims.get("email", String.class),
                roleNames,
                permissionCodes,
                tokenVersion == null ? null : tokenVersion.intValue()
        );
    }

    private PrivateKey privateKey() {
        try {
            byte[] key = Base64.getDecoder()
                    .decode(requiredProperty(properties.getPrivateKeyBase64(), "security.jwt.private-key-base64"));
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load JWT private key", exception);
        }
    }

    private RSAPublicKey publicKey() {
        try {
            byte[] key = Base64.getDecoder()
                    .decode(requiredProperty(properties.getPublicKeyBase64(), "security.jwt.public-key-base64"));
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
            return (RSAPublicKey) publicKey;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load JWT public key", exception);
        }
    }

    private String requiredProperty(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be configured");
        }
        return value;
    }
}
