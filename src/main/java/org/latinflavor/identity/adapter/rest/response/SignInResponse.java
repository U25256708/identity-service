package org.latinflavor.identity.adapter.rest.response;

import java.util.Set;

public record SignInResponse(String accessToken, long expiresIn, Set<String> permissions) {
}
