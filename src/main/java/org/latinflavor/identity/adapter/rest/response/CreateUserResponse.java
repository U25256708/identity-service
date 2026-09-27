package org.latinflavor.identity.adapter.rest.response;

import org.latinflavor.identity.domain.model.UserType;

import java.util.Set;
import java.util.UUID;


public record CreateUserResponse(UUID id, UserType userType, String employeeCode,
                                 boolean active, Set<String> roles, Set<String> permissions) {

}
