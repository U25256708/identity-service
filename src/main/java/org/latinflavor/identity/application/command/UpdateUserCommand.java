package org.latinflavor.identity.application.command;

public record UpdateUserCommand(
        String firstName,
        String paternalLastName,
        String maternalLastName,
        String phoneNumber
) {
}
