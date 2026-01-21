package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserRequest;
import ru.practicum.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserRequest userRequest) {
        UserDto userDto = userService.addUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(userService.getUsers(ids, from, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
