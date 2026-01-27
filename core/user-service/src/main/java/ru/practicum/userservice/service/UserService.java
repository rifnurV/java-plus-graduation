package ru.practicum.userservice.service;

import ru.practicum.userservice.model.dto.UserDto;
import ru.practicum.userservice.model.dto.UserRequest;

import java.util.List;

public interface UserService {
    UserDto addUser(UserRequest userRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers(List<Long> ids);

    boolean existsByEmail(String email);
}
