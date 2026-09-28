package org.latinflavor.identity.shared.pagination;

import org.latinflavor.identity.domain.errors.PaginationErrors;
import org.latinflavor.identity.shared.exception.ApplicationException;

import java.util.List;
import java.util.Locale;

public final class SortParser {

    private SortParser() {
    }

    public static List<SortSpec> parse(List<String> rawSorts) {
        if (rawSorts == null) {
            return List.of();
        }

        return rawSorts.stream()
                .filter(sort -> sort != null && !sort.isBlank())
                .map(SortParser::toSortSpec)
                .toList();
    }

    private static SortSpec toSortSpec(String rawSort) {
        String[] parts = rawSort.split(",", -1);
        if (parts.length > 2) {
            throw new ApplicationException(PaginationErrors.INVALID_SORT_FORMAT, rawSort);
        }

        String property = parts[0].strip();
        if (property.isEmpty()) {
            throw new ApplicationException(PaginationErrors.INVALID_SORT_FORMAT, rawSort);
        }

        SortSpec.Direction direction = parts.length == 2
                ? toDirection(parts[1], rawSort)
                : SortSpec.Direction.ASC;

        return new SortSpec(property, direction);
    }

    private static SortSpec.Direction toDirection(String rawDirection, String rawSort) {
        try {
            return SortSpec.Direction.valueOf(rawDirection.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(PaginationErrors.INVALID_SORT_FORMAT, rawSort);
        }
    }
}
