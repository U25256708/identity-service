package org.latinflavor.identity.adapter.rest.response;

import java.util.Set;
import java.util.UUID;

public record GetUserResponse(UUID id, String username, String firstName,
                              String paternalLastName, String maternalLastName, String email,
                              String phoneNumber, String dni, String employeeCode, boolean active,
                              Set<String> permissions) {
}
