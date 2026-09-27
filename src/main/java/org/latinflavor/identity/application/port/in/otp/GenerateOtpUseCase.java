package org.latinflavor.identity.application.port.in.otp;

public interface GenerateOtpUseCase {
    void generateCode(String email);
}
