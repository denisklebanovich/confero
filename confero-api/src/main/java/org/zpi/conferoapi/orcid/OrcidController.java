package org.zpi.conferoapi.orcid;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.OrcidApi;
import org.openapitools.model.OrcidInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrcidController implements OrcidApi {
    private final OrcidService orcidService;


    @Override
    public ResponseEntity<OrcidInfoResponse> getOrcidData(String orcid) {
        return ResponseEntity.ok(orcidService.getRecord(orcid));
    }
}
