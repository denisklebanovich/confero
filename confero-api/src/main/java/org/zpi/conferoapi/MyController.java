package org.zpi.conferoapi;

import org.openapitools.api.ApplicationApi;
import org.openapitools.model.ApplicationPreviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
class MyController implements ApplicationApi {


    @Override
    public ResponseEntity<List<ApplicationPreviewResponse>> getApplications() {
        return ResponseEntity.of(Optional.of(new ArrayList<>()));
    }
}