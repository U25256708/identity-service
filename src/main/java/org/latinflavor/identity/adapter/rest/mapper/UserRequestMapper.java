package org.latinflavor.identity.adapter.rest.mapper;

import org.latinflavor.identity.adapter.rest.request.CreateUserRequest;
import org.latinflavor.identity.adapter.rest.request.UpdateUserRequest;
import org.latinflavor.identity.adapter.rest.request.UpdateInternalAccessRequest;
import org.latinflavor.identity.adapter.rest.response.CreateUserResponse;
import org.latinflavor.identity.adapter.rest.response.GetUserResponse;
import org.latinflavor.identity.application.command.CreateUserCommand;
import org.latinflavor.identity.application.command.UpdateUserCommand;
import org.latinflavor.identity.application.command.UpdateInternalAccessCommand;
import org.latinflavor.identity.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {

    UserRequestMapper INSTANCE = Mappers.getMapper(UserRequestMapper.class);

    CreateUserCommand toCommand(CreateUserRequest request);

    UpdateUserCommand toCommand(UpdateUserRequest request);

    UpdateInternalAccessCommand toCommand(UpdateInternalAccessRequest request);

    @Mappings({
            @Mapping(target = "roles", source = "roleNames"),
            @Mapping(target = "permissions", source = "permissionCodes")
    })
    CreateUserResponse toResponse(User user);

    @Mappings({
            @Mapping(target = "permissions", source = "permissionCodes")
    })
    GetUserResponse toRetrievalResponse(User user);
}
