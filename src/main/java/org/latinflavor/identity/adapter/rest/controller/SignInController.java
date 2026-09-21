package org.latinflavor.identity.adapter.rest.controller;

import org.latinflavor.identity.adapter.rest.request.SignInRequest;
import org.latinflavor.identity.adapter.rest.response.SignInResponse;
import org.latinflavor.identity.service.authentication.factory.AuthenticationTokenFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignInController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/sign-in")
    ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        Authentication authenticationRequest = AuthenticationTokenFactory.create(request);
        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
        return ResponseEntity.noContent().build();
    }

}
