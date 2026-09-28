package org.latinflavor.identity.shared.pagination;

import org.latinflavor.identity.domain.errors.PaginationErrors;
import org.latinflavor.identity.shared.exception.ApplicationException;

import java.util.List;

public record PaginationRequest(int page, int size, List<SortSpec> sort) {

    public static final int MAX_SIZE = 100;

    public PaginationRequest {
        if (page < 0) {
            throw new ApplicationException(PaginationErrors.INVALID_PAGE);
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new ApplicationException(PaginationErrors.INVALID_PAGE_SIZE);
        }
        sort = sort == null ? List.of() : List.copyOf(sort);
    }

    public static PaginationRequest of(int page, int size, List<SortSpec> sort) {
        return new PaginationRequest(page, size, sort);
    }
}
