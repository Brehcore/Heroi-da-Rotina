package br.com.coretech.hero_api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ResourceExceptionHandler {

    // Função auxiliar simples para limpar inputs perigosos sem estragar os acentos do JSON
    private String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>]", ""); // Remove apenas as tags que abrem margem para scripts
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        String errorMessage = "Violação de integridade de dados. Tente novamente mais tarde.";

        Throwable rootCause = e.getMostSpecificCause();
        if (rootCause.getMessage().toLowerCase().contains("email")) {
            errorMessage = "O e-mail informado já está em uso.";
        }

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Violação de Integridade",
                errorMessage, // <-- Sem HtmlUtils, o JSON fica com os acentos perfeitos!
                sanitize(request.getRequestURI()) // <-- Apenas removemos < > para a IDE parar de gritar sobre XSS
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {

        StandardError err = new StandardError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                sanitize(e.getMessage()),
                sanitize(request.getRequestURI())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<StandardError> handleInsufficientBalance(InsufficientBalanceException e, HttpServletRequest request) {

        StandardError err = new StandardError(
                Instant.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Business Rule Violation",
                sanitize(e.getMessage()),
                sanitize(request.getRequestURI())
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardError> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = e instanceof BadCredentialsException ? "Email ou senha incorretos" : e.getMessage();

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Erro de autenticação",
                sanitize(message),
                sanitize(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(err);
    }
}