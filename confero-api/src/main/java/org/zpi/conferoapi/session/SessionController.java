package org.zpi.conferoapi.session;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.SessionApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.security.SecurityUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Transactional
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SessionController implements SessionApi {

    SessionService sessionService;
    SecurityUtils securityUtils;

    @Override
    public ResponseEntity<List<SessionPreviewResponse>> getSessions() {
        log.info("User requested all sessions");
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @Override
    public ResponseEntity<List<SessionPreviewResponse>> getManagableSessions() {
        log.info("User {} requested managable sessions", securityUtils.getCurrentUser());
        return ResponseEntity.ok(sessionService.getManagableSessions());
    }

    @Override
    public ResponseEntity<List<SessionPreviewResponse>> getPersonalAgenda() {
        log.info("User {} requested personal agenda", securityUtils.getCurrentUser());
        return ResponseEntity.ok(sessionService.getPersonalAgenda());
    }


    @Override
    public ResponseEntity<Void> addSessionToAgenda(AddSessionToAgendaRequest addSessionToAgendaRequest) {
        log.info("User {} requested to add session {} to agenda", securityUtils.getCurrentUser(), addSessionToAgendaRequest.getSessionId());
        sessionService.addSessionToAgenda(addSessionToAgendaRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> removeSessionFromAgenda(RemoveSessionFromAgendaRequest removeSessionFromAgendaRequest) {
        log.info("User {} requested to remove session {} from agenda", securityUtils.getCurrentUser(), removeSessionFromAgendaRequest.getSessionId());
        sessionService.removeSessionFromAgenda(removeSessionFromAgendaRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SessionResponse> getSession(Long sessionId) {
        log.info("Requested session with id {}", sessionId);
        var session = sessionService.getSession(sessionId);
        log.info("Responding with session: {}", session);
        return ResponseEntity.ok(session);
    }

    @Override
    public ResponseEntity<SessionResponse> getSessionPreview(Long sessionId) {
        log.info("Unauthenticated user requested session preview with id {}", sessionId);
        return ResponseEntity.ok(sessionService.getSessionPreview(sessionId));
    }

    @Override
    public ResponseEntity<SessionResponse> updateSession(Long sessionId, UpdateSessionRequest updateSessionRequest) {
        log.info("User {} requested to update session with id {} and request {}", securityUtils.getCurrentUser(), sessionId, updateSessionRequest);
        var session = sessionService.updateSession(sessionId, updateSessionRequest);
        log.info("Responding with updated session: {}", session);
        return ResponseEntity.ok(session);
    }

    @Override
    public ResponseEntity<Integer> addAllSessionsByOrganizerToAgenda(Long organizerId) {
        log.info("User {} requested to add all sessions by organizer with id {} to agenda", securityUtils.getCurrentUser(), organizerId);
        var added = sessionService.addAllSessionsByOrganizerToAgenda(organizerId);
        log.info("Responding with number of sessions added: {}", added);
        return ResponseEntity.ok(added);
    }

    @Override
    public ResponseEntity<AttachmentResponse> addPresentationAttachment(Long sessionId, Long presentationId, MultipartFile file) {
        log.info("User {} requested to add attachment to presentation with id {} in session with id {} and filename {}",
                securityUtils.getCurrentUser(), presentationId, sessionId, file.getOriginalFilename());
        var attachment = sessionService.addPresentationAttachment(sessionId, presentationId, file);
        log.info("Responding with attachment: {}", attachment);
        return ResponseEntity.ok(attachment);
    }

    @Override
    public ResponseEntity<Void> deletePresentationAttachment(Long sessionId, Long attachmentId, Long presentationId) {
        log.info("User {} requested to delete attachment with id {} from presentation with id {} in session with id {}",
                securityUtils.getCurrentUser(), attachmentId, presentationId, sessionId);
        sessionService.deletePresentationAttachment(sessionId, attachmentId, presentationId);
        log.info("Attachment deleted successfully");
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<List<SesssionEventResponse>> getSessionEvents(Integer pageSize) {
        log.info("User {} requested session events with page size {}", securityUtils.getCurrentUser(), pageSize);
        var events = sessionService.getSessionEvents(pageSize);
        log.info("Responding with session events: {}", events);
        return ResponseEntity.ok(events);
    }
}
