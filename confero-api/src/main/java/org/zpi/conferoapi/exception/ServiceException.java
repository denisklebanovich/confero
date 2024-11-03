package org.zpi.conferoapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.openapitools.model.ErrorReason;

@Getter
@AllArgsConstructor
public class ServiceException extends RuntimeException {
    private final ErrorReason reason;

    public HttpStatus httpStatus() {
        return switch (reason) {
            case ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS -> HttpStatus.CONFLICT;
        };
    }
}
