package org.latinflavor.identity.application.port.out.external;

public interface TokenPort {
    IssuedToken generate(TokenSubject subject);

    TokenSubject validate(String token);
}
