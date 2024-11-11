package org.zpi.conferoapi.session;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.SessionApi;
import org.openapitools.model.AddSessionToAgendaRequest;
import org.openapitools.model.SessionPreviewResponse;
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
}
