package ro.occam.mp4analyzer.controllers;

import ro.occam.mp4analyzer.dto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ro.occam.mp4analyzer.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
@RestController
public class ExceptionController {

    /**
     * Handler for generic IOExceptions and RuntimeExceptions
     * Any IO or runtime that occurs while processing the file is reported back to the client
     *
     * @param ex MissingRequestHeaderException
     * @return String - a human readable error message
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({IOException.class, RuntimeException.class})
    public ValidationError handleIOException(Exception ex) {
        return ValidationError.builder()
                .field(Constants.X_VIDEO_URL_REQUEST_HEADER)
                .error("There was an unexpected error while processing the contents of the file: " +
                    ex.getMessage())
                .build();
    }

    /**
     * Missing request header exception handler
     *
     * @param ex MissingRequestHeaderException
     * @return String - a human readable error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingRequestHeaderException.class})
    public ValidationError handleConstraintViolation(MissingRequestHeaderException ex) {
        return ValidationError.builder()
                .field(Constants.X_VIDEO_URL_REQUEST_HEADER)
                .error(ex.getMessage())
                .build();
    }

    /**
     * Constraint violation exception handler
     *
     * @param ex ConstraintViolationException
     * @return List<ValidationError> - list of ValidationError built from set of ConstraintViolation
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public List<ValidationError> handleConstraintViolation(ConstraintViolationException ex) {
        return buildValidationErrors(ex.getConstraintViolations());
    }

    /**
     * Build list of ValidationError from set of ConstraintViolation
     *
     * @param violations Set<ConstraintViolation<?>> - Set of parameterized ConstraintViolations
     * @return List<ValidationError> - list of validation errors
     */
    private List<ValidationError> buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(violation -> ValidationError.builder()
                        .field(StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                                .reduce((first, second) -> second)
                                .orElse(null)
                                .toString())
                        .error(violation.getMessage())
                        .build())
                .collect(toList());
    }
}
