package org.zpi.conferoapi.session;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.SessionPreviewResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public List<SessionPreviewResponse> getSessions() {
        var mock = new SessionPreviewResponse()
                .id(1L)
                .title("Test")
                .addTagsItem("AI")
                .addTagsItem("ML")
                .addTagsItem("Facial Recognition")
                .addTagsItem("Deep Learning")
                .addTagsItem("Computer Vision")
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(3600));
        return List.of(mock);
    }

    public SessionPreviewResponse createSession(CreateApplicationRequest createApplicationRequest) {
        return new SessionPreviewResponse()
                .id(2L)
                .title("Test")
                .addTagsItem("AI")
                .addTagsItem("ML")
                .addTagsItem("Facial Recognition")
                .addTagsItem("Deep Learning")
                .addTagsItem("Computer Vision")
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(3600));
    }
}
