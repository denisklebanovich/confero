package org.zpi.conferoapi.exception;


import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        return ResponseEntity
                .status(ex.httpStatus())
                .body(new ErrorResponse().reason(ex.getReason()));
    }
}
