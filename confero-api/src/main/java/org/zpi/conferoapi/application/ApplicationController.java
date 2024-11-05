package org.zpi.conferoapi.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.ApplicationApi;
import org.openapitools.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.zpi.conferoapi.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Transactional
@Slf4j
class ApplicationController implements ApplicationApi {

    ApplicationService applicationService;
    UserRepository userRepository;

    @Override
    public ResponseEntity<ApplicationPreviewResponse> createApplication(CreateApplicationRequest createApplicationRequest) {
        log.info("User requested to create an application with the following data: {}", createApplicationRequest);

        var session = applicationService.createApplication(createApplicationRequest);

        log.info("Application created successfully: {}", session);

        return new ResponseEntity<>(
                new ApplicationPreviewResponse()
                        .id(session.getId())
                        .title(session.getTitle())
                        .type(session.getType())
                        .status(session.getStatus())
                        .presenters(session.getPresentations().stream().flatMap(presentation -> presentation.getPresenters().stream())
                                .map(presenter -> new PresenterResponse()
                                        .id(presenter.getId())
                                        .name(presenter.getName())
                                        .surname(presenter.getSurname())
                                        .orcid(presenter.getOrcid())
                                        .isSpeaker(presenter.getIsMain() == Boolean.TRUE)
                                )
                                .collect(Collectors.toList()))
                , HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteApplication(Long applicationId) {
        return ApplicationApi.super.deleteApplication(applicationId);
    }

    @Override
    public ResponseEntity<ApplicationResponse> getApplication(Long applicationId) {
        return ResponseEntity.ok(applicationService.getApplication(applicationId));
    }

    @Override
    public ResponseEntity<List<ApplicationPreviewResponse>> getApplications() {
        return ApplicationApi.super.getApplications();
    }

    @Override
    public ResponseEntity<ApplicationPreviewResponse> reviewApplication(Long applicationId, ReviewRequest reviewRequest) {
        return ApplicationApi.super.reviewApplication(applicationId, reviewRequest);
    }

    @Override
    public ResponseEntity<ApplicationPreviewResponse> updateApplication(Long applicationId, UpdateApplicationRequest updateApplicationRequest) {
        return ApplicationApi.super.updateApplication(applicationId, updateApplicationRequest);
    }
}