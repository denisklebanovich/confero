package org.zpi.conferoapi;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.PresentationRequest;
import org.openapitools.model.PresenterRequest;
import org.openapitools.model.SessionType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.zpi.conferoapi.application.ApplicationService;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ConferenceEditionRepository conferenceEditionRepository;
    private final ApplicationService applicationService;

    @Override
    public void run(String... args) throws Exception {
        User user = initUser();
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(), null, null));
        initConferenceEdition();
        initApplication(user);
    }

    private User initUser() {
        User user = User
                .builder()
                .email("denis.klebanovich@gmail.com")
                .orcid("0009-0005-9044-6202")
                .emailVerified(true)
                .isAdmin(true)
                .build();
        userRepository.save(user);
        return user;
    }

    private void initConferenceEdition() {
        ConferenceEdition conferenceEdition = ConferenceEdition
                .builder()
                .createdAt(Instant.now())
                .applicationDeadlineTime(Instant.now().plus(180, java.time.temporal.ChronoUnit.DAYS))
                .build();
        conferenceEditionRepository.save(conferenceEdition);

    }

    private void initApplication(User user) {
        var presentationRequest = new PresentationRequest()
                .title("Introduction to AI")
                .addPresentersItem(new PresenterRequest()
                        .email("presenter1@example.com")
                        .isSpeaker(true)
                        .orcid(user.getOrcid()));

        var createApplicationRequest = new CreateApplicationRequest()
                .title("AI in Modern Science")
                .type(SessionType.WORKSHOP)
                .tags(List.of("AI", "Technology"))
                .description("A deep dive into AI's impact on science")
                .presentations(List.of(presentationRequest))
                .saveAsDraft(false);

        //log json request
        System.out.println(createApplicationRequest.toString());
        applicationService.createApplication(createApplicationRequest);

    }
}
