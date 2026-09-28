package org.latinflavor.identity.application.factory;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import org.latinflavor.identity.application.command.SearchInternalUsersCriteria;
import org.latinflavor.identity.domain.errors.UserErrors;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;
import static org.latinflavor.identity.domain.model.UserType.INTERNAL;

@Component
public class UserSearchSpecificationFactory {

    private static final List<String> SEARCHABLE_FIELDS = List.of(
            "username",
            "firstName",
            "paternalLastName",
            "maternalLastName",
            "email",
            "phoneNumber",
            "dni",
            "employeeCode"
    );

    public Specification<User> internalUsersMatching(SearchInternalUsersCriteria criteria) {
        return isInternal()
                .and(matchesQuery(criteria))
                .and(withAccessData());
    }

    private Specification<User> isInternal() {
        return (root, query, builder) -> builder.equal(root.get("userType"), INTERNAL);
    }

    private Specification<User> withAccessData() {
        return (root, query, builder) -> {
            if (isSelectQuery(query)) {
                root.fetch("roles", JoinType.LEFT);
                root.fetch("permissions", JoinType.LEFT);
                query.distinct(true);
            }
            return builder.conjunction();
        };
    }

    private boolean isSelectQuery(CriteriaQuery<?> query) {
        return query != null && !Long.class.equals(query.getResultType());
    }

    private Specification<User> matchesQuery(SearchInternalUsersCriteria criteria) {
        if (criteria == null) return null;

        String q = criteria.q();
        if (q == null || q.isBlank()) {
            return null;
        }

        String pattern = "%" + escapeLike(q.strip().toLowerCase(Locale.ROOT)) + "%";
        return fieldsToSearch(criteria.filters()).stream()
                .map(field -> containsIgnoreCase(field, pattern))
                .reduce(Specification::or)
                .orElse(null);
    }

    private Set<String> fieldsToSearch(List<String> filters) {
        if (filters == null) {
            return new LinkedHashSet<>(SEARCHABLE_FIELDS);
        }

        Set<String> fields = filters.stream()
                .filter(filter -> filter != null && !filter.isBlank())
                .map(this::resolveField)
                .collect(toCollection(LinkedHashSet::new));

        return fields.isEmpty() ? new LinkedHashSet<>(SEARCHABLE_FIELDS) : fields;
    }

    private Specification<User> containsIgnoreCase(String field, String pattern) {
        return (root, query, builder) -> builder.like(builder.lower(root.get(field)), pattern, '\\');
    }

    private String resolveField(String filter) {
        String normalizedFilter = filter.replace("_", "")
                .replace("-", "")
                .strip();
        return SEARCHABLE_FIELDS.stream()
                .filter(field -> field.equalsIgnoreCase(normalizedFilter))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(UserErrors.UNSUPPORTED_USER_SEARCH_FILTER, filter));
    }

    private String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
