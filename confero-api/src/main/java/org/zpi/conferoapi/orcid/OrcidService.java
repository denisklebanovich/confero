package org.zpi.conferoapi.orcid;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.OrcidInfoResponse;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.exception.ServiceException;

import java.util.concurrent.CompletableFuture;

import static org.openapitools.model.ErrorReason.INVALID_ORCID;

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


    public CompletableFuture<OrcidInfoResponse> getOrcidInfoAsync(String orcid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getRecord(orcid);
            } catch (Exception e) {
                throw new ServiceException(INVALID_ORCID);
            }
        });
    }
}
