package org.zpi.conferoapi.session;


import lombok.RequiredArgsConstructor;
import org.openapitools.api.SessionApi;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.SessionPreviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SessionController implements SessionApi {
    private final SessionService sessionService;

    @Override
    public ResponseEntity<List<SessionPreviewResponse>> getSessions() {
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @Override
    public ResponseEntity<SessionPreviewResponse> createSession(CreateApplicationRequest createApplicationRequest) {
        return ResponseEntity.ok(sessionService.createSession(createApplicationRequest));
    }
}
