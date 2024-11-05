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
            case ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS, EMAIL_CANNOT_BE_UPDATED -> HttpStatus.CONFLICT;
            case USER_NOT_FOUND, APPLICATION_NOT_FOUND, SESSION_NOT_FOUND, NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_FILE_FORMAT, NO_ACTIVE_CONFERENCE_EDITION -> HttpStatus.BAD_REQUEST;
            case S3_UPLOAD_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
