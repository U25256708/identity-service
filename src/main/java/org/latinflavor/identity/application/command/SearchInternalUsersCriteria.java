package org.latinflavor.identity.application.command;

import java.util.List;

public record SearchInternalUsersCriteria(
        String q,
        List<String> filters
) {
}
