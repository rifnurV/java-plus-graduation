package ru.practicum.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    // Сообщение об ошибке
    private String message;

    // Краткое описание причины
    private String reason;

    // HTTP-статус (в виде строки, например, "CONFLICT" или "NOT_FOUND")
    private String status;

    // Дата и время ошибки в формате "yyyy-MM-dd HH:mm:ss"
    @Builder.Default
    private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
}