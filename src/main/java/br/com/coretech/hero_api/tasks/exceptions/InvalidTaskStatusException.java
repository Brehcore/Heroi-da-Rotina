package br.com.coretech.hero_api.tasks.exceptions;

import java.io.Serial;

public class InvalidTaskStatusException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTaskStatusException(String message) {
        super(message);
    }}
