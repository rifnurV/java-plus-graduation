package ru.practicum.userservice.model.mapper;

import ru.practicum.userservice.model.User;
import ru.practicum.userservice.model.dto.UserDto;
import ru.practicum.userservice.model.dto.UserRequest;
import ru.practicum.userservice.model.dto.UserShortDto;

public class UserMapper {
    public static User toUser(UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        return user;
    }

    public static User toUser(UserDto userRequest) {
        User user = new User();
        user.setId(userRequest.getId());
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }

    public static UserShortDto toShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }

}
