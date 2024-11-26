package org.zpi.conferoapi.organizer;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.OrganizerApi;
import org.openapitools.model.OrganizerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.zpi.conferoapi.orcid.OrcidService;
import org.zpi.conferoapi.security.SecurityUtils;

import java.util.List;

@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@RestController
public class OrganizerController implements OrganizerApi {

    OrganizerService organizerService;
    SecurityUtils securityUtils;

    @Override
    public ResponseEntity<List<OrganizerResponse>> findOrganizers(String searchQuery) {
        log.info("User {} is searching for organizers with query: {}", securityUtils.getCurrentUser(), searchQuery);
        var organizers = organizerService.findOrganizers(searchQuery);
        log.info("Responding with following organizers: {}", organizers);
        return ResponseEntity.ok(organizers);
    }

    @Override
    public ResponseEntity<List<OrganizerResponse>> massUpdateOrganizers() {
        log.info("User {} is updating all organizers", securityUtils.getCurrentUser());
        var organizers = organizerService.massUpdateOrganizers();
        return ResponseEntity.ok(organizers);
    }
}
