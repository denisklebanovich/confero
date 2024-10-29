package org.zpi.conferoapi.application;

import org.openapitools.api.ApplicationApi;
import org.openapitools.model.ApplicationPreviewResponse;
import org.openapitools.model.CreateApplicationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


@RestController
class ApplicationController implements ApplicationApi {

    @Override
    public ResponseEntity<ApplicationPreviewResponse> createApplication(CreateApplicationRequest createApplicationRequest) {
        return null;
    }

    @Override
    public ResponseEntity<List<ApplicationPreviewResponse>> getApplications() {
        return new ResponseEntity<>(Collections.emptyList(), null, 200);
    }


}