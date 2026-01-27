package ru.practicum.userservice.exception;

public class PaginationException extends RuntimeException {
    public PaginationException(String message) {
        super(message);
    }
}