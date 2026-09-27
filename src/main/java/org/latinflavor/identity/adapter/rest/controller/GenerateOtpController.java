package org.latinflavor.identity.adapter.rest.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.latinflavor.identity.application.port.in.otp.GenerateOtpUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class GenerateOtpController {

    private final GenerateOtpUseCase useCase;

    @PostMapping("/generate-code")
    private ResponseEntity<String> generateCode(@RequestBody GenerateOtpRequest generateOtpRequest) {
        String code = useCase.generateCode(generateOtpRequest.email());
        return ResponseEntity.ok().body(code);
    }

    record GenerateOtpRequest(@NotBlank @Email String email, String code) {
    }


}
