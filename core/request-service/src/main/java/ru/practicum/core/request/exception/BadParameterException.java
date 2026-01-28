package ru.practicum.core.request.exception;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String mess) {
        super(mess);
    }
}