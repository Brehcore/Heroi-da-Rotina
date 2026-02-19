package br.com.coretech.hero_api.exceptions;

import lombok.Getter;

@Getter
public class ValidationError {
    private String field;
    private String message;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
