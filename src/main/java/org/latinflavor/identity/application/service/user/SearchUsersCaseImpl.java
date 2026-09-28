package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.command.SearchInternalUsersCriteria;
import org.latinflavor.identity.application.factory.UserSearchSortFactory;
import org.latinflavor.identity.application.factory.UserSearchSpecificationFactory;
import org.latinflavor.identity.application.port.in.user.SearchUsersCase;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.pagination.PageResult;
import org.latinflavor.identity.shared.pagination.PaginationRequest;
import org.latinflavor.identity.shared.pagination.SortSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchUsersCaseImpl implements SearchUsersCase {

    private final UserSearchPort userSearchPort;
    private final UserSearchSpecificationFactory userSearchSpecificationFactory;
    private final UserSearchSortFactory userSearchSortFactory;

    @Override
    @Transactional(readOnly = true)
    public PageResult<User> searchInternalUsers(SearchInternalUsersCriteria criteria, PaginationRequest pagination) {
        List<SortSpec> sort = userSearchSortFactory.resolveSortable(pagination.sort());

        Page<User> page = userSearchPort.searchInternalUsers(userSearchSpecificationFactory.withSpecification(criteria),
                toPageable(pagination, sort));

        return PageResult.of(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    private Pageable toPageable(PaginationRequest pagination, List<SortSpec> sort) {
        List<Sort.Order> orders = sort.stream()
                .map(spec -> new Sort.Order(
                        spec.direction() == SortSpec.Direction.DESC ? Sort.Direction.DESC : Sort.Direction.ASC,
                        spec.property()))
                .toList();
        return PageRequest.of(pagination.page(), pagination.size(), Sort.by(orders));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchInternalUsers() {
        return userSearchPort.findAllInternalWithDetails();
    }
}
