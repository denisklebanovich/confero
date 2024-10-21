package org.zpi.conferoapi.orcid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orcid")
@RequiredArgsConstructor
public class OrcidController {
    private final OrcidService orcidService;

    @GetMapping("/{id}")
    public OrcidInfo getRecord(@PathVariable String id) {
        return orcidService.getRecord(id);
    }
}
