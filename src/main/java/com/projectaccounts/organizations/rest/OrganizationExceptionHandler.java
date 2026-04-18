package com.projectaccounts.organizations.rest;

import com.projectaccounts.organizations.dto.ErrorResponse;
import com.projectaccounts.organizations.service.OrganizationQueryService;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrganizationExceptionHandler {

    @ExceptionHandler(OrganizationQueryService.OrganizationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrganizationQueryService.OrganizationNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("ORGANIZATION_NOT_FOUND", ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AggregateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAggregateNotFound(AggregateNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("ORGANIZATION_NOT_FOUND", ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<ErrorResponse> handleCommandExecutionException(CommandExecutionException ex) {
        ErrorResponse error = new ErrorResponse("COMMAND_FAILED", ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message, null, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred", null, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
