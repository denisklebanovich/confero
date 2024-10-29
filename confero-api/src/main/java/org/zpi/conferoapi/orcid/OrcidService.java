package org.zpi.conferoapi.orcid;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.OrcidInfoResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrcidService {
    private final OrcidClient orcidClient;

    public OrcidInfoResponse getRecord(String id) {
        var orcidXMLResponse = orcidClient.getRecord(id);
        return new OrcidInfoResponse().orcid(id)
                .name(orcidXMLResponse.getName())
                .surname(orcidXMLResponse.getSurname());
    }
}
