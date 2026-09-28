package org.latinflavor.identity.application.port.in.user;

import org.latinflavor.identity.application.command.SearchInternalUsersCriteria;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.pagination.PageResult;
import org.latinflavor.identity.shared.pagination.PaginationRequest;

import java.util.List;

public interface SearchUsersCase {

    List<User> searchInternalUsers();

    PageResult<User> searchInternalUsers(SearchInternalUsersCriteria criteria, PaginationRequest pagination);

}
