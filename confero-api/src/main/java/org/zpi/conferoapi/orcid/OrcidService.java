package org.zpi.conferoapi.orcid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrcidService {
    private final OrcidClient orcidClient;

    public OrcidInfo getRecord(String id) {
        var orcidXMLResponse = orcidClient.getRecord(id);
        return new OrcidInfo(
                id,
                orcidXMLResponse.getName(),
                orcidXMLResponse.getSurname()
        );
    }
}
