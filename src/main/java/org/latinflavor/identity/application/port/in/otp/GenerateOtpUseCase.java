package org.latinflavor.identity.application.port.in.otp;

public interface GenerateOtpUseCase {
    String generateCode(String email);
}
