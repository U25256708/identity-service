package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.factory.UserSearchSpecificationFactory;
import org.latinflavor.identity.application.port.in.user.GetUsersUseCase;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchUsersUseCaseImpl implements GetUsersUseCase {

    private final UserSearchPort userSearchPort;
    private final UserSearchSpecificationFactory userSearchSpecificationFactory;

//    @Override
//    @Transactional(readOnly = true)
//    public Page<User> searchInternalUsers(SearchInternalUsersCriteria criteria, Pageable pageable) {
//        return userSearchPort.searchInternalUsers(userSearchSpecificationFactory.internalUsersMatching(criteria), pageable);
//    }

    @Override
    public List<User> searchInternalUsers() {
        return userSearchPort.findAllInternalWithDetails();
    }
}
