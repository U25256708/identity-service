package org.latinflavor.identity.adapter.rest.response;

public record SignInResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String type
) {
    public SignInResponse(String accessToken, String refreshToken, long expiresIn) {
        this(accessToken, refreshToken, expiresIn, "Bearer");
    }
}
