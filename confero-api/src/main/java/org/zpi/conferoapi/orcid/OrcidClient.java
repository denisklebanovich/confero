package org.zpi.conferoapi.orcid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "orcid", url = "https://pub.orcid.org/v3.0")
public interface OrcidClient {
    String ORCID_MEDIA_TYPE = "application/vnd.orcid+xml";

    @GetMapping(value = "/{id}/record", produces = ORCID_MEDIA_TYPE)
    OrcidXMLRecord getRecord(@PathVariable("id") String id);
}
