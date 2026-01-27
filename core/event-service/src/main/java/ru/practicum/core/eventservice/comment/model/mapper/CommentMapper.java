package ru.practicum.core.eventservice.comment.model.mapper;

import ru.practicum.core.eventservice.comment.model.Comment;
import ru.practicum.core.eventservice.comment.model.dto.CommentRequest;
import ru.practicum.core.eventservice.comment.model.dto.CommentResponse;
import ru.practicum.core.eventservice.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentMapper {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Comment toComment(CommentRequest commentRequest, Long author, Event event) {
        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        return comment;
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setText(comment.getText());
        commentResponse.setCreated(comment.getCreated().format(TIME_FORMAT));
        commentResponse.setAuthor(comment.getAuthor());
        commentResponse.setAuthor(comment.getAuthor());
        commentResponse.setEvent(comment.getEvent().getId());
        return commentResponse;
    }
}
