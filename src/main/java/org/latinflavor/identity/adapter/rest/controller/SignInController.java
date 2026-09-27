package org.latinflavor.identity.adapter.rest.controller;

import org.latinflavor.identity.adapter.rest.request.SignInRequest;
import org.latinflavor.identity.adapter.rest.response.SignInResponse;
import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.external.IssuedToken;
import org.latinflavor.identity.application.service.auth.AuthenticationTokenFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SignInController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/sign-in")
    ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        Authentication authenticationRequest = AuthenticationTokenFactory.create(request);
        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
        IssuedToken issuedToken = (IssuedToken) authenticationResult.getPrincipal();
        return ResponseEntity.ok(new SignInResponse(
                issuedToken.accessToken(), issuedToken.expiresIn(), issuedToken.permissions()));
    }

}
