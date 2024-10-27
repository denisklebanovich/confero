package org.zpi.conferoapi;

import org.openapitools.api.ApplicationApi;
import org.openapitools.model.ApplicationPreviewResponse;
import org.openapitools.model.CreateApplicationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
class ApplicationController implements ApplicationApi {

    @Override
    public ResponseEntity<ApplicationPreviewResponse> createApplication(CreateApplicationRequest createApplicationRequest) {
        return null;
    }
}