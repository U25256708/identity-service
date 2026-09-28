package org.latinflavor.identity.application.port.out.external.jwt;

public interface TokenPort {
    IssuedToken generate(TokenSubject subject);

    TokenSubject validate(String token);
}
