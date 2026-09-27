package org.latinflavor.identity.adapter.rest.response;

import org.latinflavor.identity.domain.model.RoleName;

import java.util.Set;
import java.util.UUID;

public record GetUserResponse(UUID id, String username, String firstName,
                              String paternalLastName, String maternalLastName, String email,
                              String phoneNumber, String dni, String employeeCode, boolean active,
                              Set<RoleName> roles, Set<String> permissions) {
}
