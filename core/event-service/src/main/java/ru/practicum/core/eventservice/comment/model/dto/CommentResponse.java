package ru.practicum.core.eventservice.comment.model.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;
    String text;
    String created;
    Long author;
    Long event;
}
