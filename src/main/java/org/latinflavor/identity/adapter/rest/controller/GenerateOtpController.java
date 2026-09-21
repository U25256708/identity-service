package org.latinflavor.identity.adapter.rest.controller;

import org.latinflavor.identity.adapter.rest.request.GenerateOtpRequest;
import org.latinflavor.identity.service.otp.GenerateOtpService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class GenerateOtpController {

    private final GenerateOtpService service;

    @PostMapping("/generate-code")
    private ResponseEntity<GenerateOtpResponse> generateCode(@RequestBody GenerateOtpRequest generateOtpRequest) {
        service.generateCode(generateOtpRequest.email());
        return ResponseEntity.ok(new GenerateOtpResponse(generateOtpRequest.email(), "Success"));
    }

    record GenerateOtpResponse(String email, String message) {
    }


}
