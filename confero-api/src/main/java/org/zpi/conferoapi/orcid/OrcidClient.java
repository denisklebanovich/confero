package org.zpi.conferoapi.orcid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "orcid", url = "https://orcid.org")
public class OrcidClient {
}
