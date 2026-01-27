package ru.practicum.core.eventservice.client;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.core.eventservice.client.dto.UserDto;


import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UserClientFallback implements UserClient {


    @Override
    public ResponseEntity<UserDto> getUserId(Long userId) throws FeignException {
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<UserDto>> getUsers(List<Long> ids) throws FeignException {
        return ResponseEntity.ok(Collections.EMPTY_LIST);
    }
}
