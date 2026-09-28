package org.latinflavor.identity.application.factory;

import org.latinflavor.identity.domain.errors.PaginationErrors;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.latinflavor.identity.shared.pagination.SortSpec;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSearchSortFactory {

    private static final List<String> SORTABLE_FIELDS = List.of(
            "username",
            "firstName",
            "paternalLastName",
            "maternalLastName",
            "email",
            "phoneNumber",
            "dni",
            "employeeCode",
            "active"
    );

    private static final SortSpec DEFAULT_SORT = new SortSpec("username", SortSpec.Direction.ASC);

    public List<SortSpec> resolveSortable(List<SortSpec> sort) {
        if (sort == null || sort.isEmpty()) {
            return List.of(DEFAULT_SORT);
        }

        return sort.stream().map(this::resolveField).toList();
    }

    private SortSpec resolveField(SortSpec spec) {
        return SORTABLE_FIELDS.stream()
                .filter(field -> field.equalsIgnoreCase(spec.property()))
                .findFirst()
                .map(field -> new SortSpec(field, spec.direction()))
                .orElseThrow(() -> new ApplicationException(PaginationErrors.UNSUPPORTED_SORT_FIELD, spec.property()));
    }
}
