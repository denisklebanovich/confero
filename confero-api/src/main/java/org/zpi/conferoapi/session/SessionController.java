package org.zpi.conferoapi.session;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.SessionApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
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
        log.info("User {} requested session with id {}", securityUtils.getCurrentUser(), sessionId);
        var session = sessionService.getSession(sessionId);
        log.info("Responding with session: {}", session);
        return ResponseEntity.ok(session);
    }


    @Override
    public ResponseEntity<SessionResponse> updateSession(Long sessionId, UpdateSessionRequest updateSessionRequest) {
        log.info("User {} requested to update session with id {} and request {}", securityUtils.getCurrentUser(), sessionId, updateSessionRequest);
        var session = sessionService.updateSession(sessionId, updateSessionRequest);
        log.info("Responding with updated session: {}", session);
        return ResponseEntity.ok(session);
    }
}
