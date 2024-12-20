package org.zpi.conferoapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openapitools.model.ErrorReason;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ServiceException extends RuntimeException {
    private final ErrorReason reason;

    public HttpStatus httpStatus() {
        return switch (reason) {
            case ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS, EMAIL_CANNOT_BE_UPDATED, EMAIL_ALREADY_EXISTS ->
                    HttpStatus.CONFLICT;
            case USER_NOT_FOUND, APPLICATION_NOT_FOUND, SESSION_NOT_FOUND, NOT_FOUND, PRESENTATION_NOT_FOUND,
                 PRESENTER_NOT_FOUND, ATTACHMENT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_FILE_FORMAT, NO_ACTIVE_CONFERENCE_EDITION, INVALID_ORCID, ADMIN_CANNOT_UPDATE_APPLICATION,
                 NO_PRESENTERS_PROVIDED, NO_PRESENTATIONS_PROVIDED, SESSION_IS_NOT_FROM_CURRENT_CONFERENCE_EDITION,
                 BOTH_START_AND_END_TIME_MUST_BE_PROVIDED_FOR_PRESENTATION_UPDATE, START_TIME_MUST_BE_BEFORE_END_TIME,
                 CONFERENCE_EDITION_CANNOT_HAVE_DEADLINE_IN_THE_PAST->
                    HttpStatus.BAD_REQUEST;
            case S3_UPLOAD_ERROR, UNEXPECTED_ERROR, EMAIL_SENDING_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ADMIN_CANNOT_CREATE_APPLICATION, ADMIN_CANNOT_DELETE_APPLICATION, FORBIDDEN,
                 ONLY_PARTICIPANT_CAN_UPDATE_PRESENTATION,
                 ONLY_PARTICIPANTS_CAN_UPDATE_SESSION -> HttpStatus.FORBIDDEN;
        };
    }
}
