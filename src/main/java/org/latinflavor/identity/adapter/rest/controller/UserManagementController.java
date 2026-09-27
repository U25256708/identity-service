package org.latinflavor.identity.adapter.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.adapter.rest.request.CreateUserRequest;
import org.latinflavor.identity.adapter.rest.request.UpdateUserRequest;
import org.latinflavor.identity.adapter.rest.response.CreateUserResponse;
import org.latinflavor.identity.adapter.rest.response.GetUserResponse;
import org.latinflavor.identity.application.port.in.user.DeleteUserUseCase;
import org.latinflavor.identity.application.port.in.user.GetUserUseCase;
import org.latinflavor.identity.application.port.in.user.GetUsersUseCase;
import org.latinflavor.identity.application.port.in.user.CreateUserUseCase;
import org.latinflavor.identity.application.port.in.user.UpdateUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.latinflavor.identity.adapter.rest.mapper.UserRequestMapper.INSTANCE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(CREATED)
                .body(INSTANCE.toResponse(createUserUseCase.create(INSTANCE.toCommand(request))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(INSTANCE.toRetrievalResponse(getUserUseCase.get(id.toString())));
    }

    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getAll() {
        return ResponseEntity.ok(getUsersUseCase.getAll()
                .stream()
                .map(INSTANCE::toRetrievalResponse)
                .toList());
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Void> update(@PathVariable UUID id,
                                       @Valid @RequestBody UpdateUserRequest request) {
        updateUserUseCase.update(id.toString(), INSTANCE.toCommand(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/disable")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUserUseCase.delete(id.toString());
        return ResponseEntity.noContent().build();
    }
}
