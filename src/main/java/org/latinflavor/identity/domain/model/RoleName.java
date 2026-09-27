package org.latinflavor.identity.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {

    BASIC("Cliente de la plataforma"),
    INTERNAL("Usuario interno con permisos básicos de gestión"),
    ADMIN("Admin de la plataforma");

    private String description;

    RoleName(String description) {
        this.description = description;
    }
}
