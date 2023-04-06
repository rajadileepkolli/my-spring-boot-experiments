package com.example.rest.webclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class ErrorDetailProblemHandlingControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onWebExchangeBindException(
            WebExchangeBindException webExchangeBindExceptionException) {
        ProblemDetail problemDetail = webExchangeBindExceptionException.getBody();
        problemDetail.setTitle(webExchangeBindExceptionException.getReason());
        List<ApiValidationError> validationErrorsList =
                webExchangeBindExceptionException.getAllErrors().stream()
                        .map(
                                objectError -> {
                                    FieldError fieldError = (FieldError) objectError;
                                    return new ApiValidationError(
                                            fieldError.getObjectName(),
                                            fieldError.getField(),
                                            fieldError.getRejectedValue(),
                                            Objects.requireNonNull(fieldError.getDefaultMessage()));
                                })
                        .sorted(Comparator.comparing(ApiValidationError::field))
                        .toList();
        problemDetail.setProperty("violations", validationErrorsList);
        return problemDetail;
    }

    record ApiValidationError(String object, String field, Object rejectedValue, String message) {}
}
