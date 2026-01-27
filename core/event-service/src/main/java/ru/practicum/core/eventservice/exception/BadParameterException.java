package ru.practicum.core.eventservice.exception;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String mess) {
        super(mess);
    }
}