package ru.practicum.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.userservice.model.dto.UserDto;
import ru.practicum.userservice.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/client/users")
@RequiredArgsConstructor
public class UserClientController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserId(@PathVariable(name = "userId") Long userId) {
        log.info("GET /internal/user/{} - Получен внутренний запрос на получение информации о пользователе", userId);
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(name = "ids") List<Long> ids) {
        log.info("GET /internal/user - Получен внутренний запрос на получение информации о пользователях");
        return ResponseEntity.ok().body(userService.getAllUsers(ids));
    }

}
