package org.zpi.conferoapi;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.zpi.conferoapi.application.ApplicationService;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.time.Instant;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Transactional
public class MockDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ConferenceEditionRepository conferenceEditionRepository;
    private final ApplicationService applicationService;
    private final UserEmailRepository userEmailRepository;
    private final SessionRepository sessionRepository;

    @Override
    public void run(String... args) throws Exception {
        User admin = initAdmin();
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(admin.getId(), null, null));
        var edition = saveCurrentEdition();
        var users = saveMockUsers();
        saveMockSessions(users, edition,admin);
    }

    private User initAdmin() {
        User user = User
                .builder()
                .name("Dzianis")
                .surname("Klebanovich")
                .orcid("0009-0005-9044-6202")
                .isAdmin(true)
                .build();
        userRepository.save(user);
        userEmailRepository.save(new UserEmail("denis.klebanovich@gmail.com", true, user, null));
        return user;
    }

    public List<User> saveMockUsers() {
        List<User> users = List.of(
                User.builder()
                        .orcid("0000-0001-2345-6789")
                        .build(),
                User.builder()
                        .orcid("0000-0002-3456-7890")
                        .build(),
                User.builder()
                        .orcid("0000-0003-4567-8901")
                        .build(),
                User.builder()
                        .orcid("0000-0004-5678-9012")
                        .build(),
                User.builder()
                        .orcid("0000-0005-6789-0123")
                        .build()
        );

        users = userRepository.saveAll(users);

        userEmailRepository.saveAll(List.of(
                new UserEmail("alice.smith@gmail.com", true, users.get(0), null),
                new UserEmail("bob.johnson@yahoo.com", true, users.get(1), null),
                new UserEmail("charlie.brown@gmail.com", true, users.get(2), null),
                new UserEmail("david.green@gmail.com", true, users.get(3), null),
                new UserEmail("emma.white@gmail.com", true, users.get(4), null)
        ));

        return users;
    }


    private ConferenceEdition saveCurrentEdition() {
        return conferenceEditionRepository.save(ConferenceEdition
                .builder()
                .createdAt(Instant.now())
                .applicationDeadlineTime(Instant.now().plus(180, java.time.temporal.ChronoUnit.DAYS))
                .build());

    }

    public List<Session> saveMockSessions(List<User> users, ConferenceEdition edition,User admin) {
        var sessions = List.of(
                Session.builder()
                        .title("Innovative Approaches in Quantum Computing")
                        .description("This session will cover the latest innovative approaches in quantum computing.")
                        .creator(admin)
                        .type(SessionType.SESSION)
                        .presentations(
                                List.of(Presentation.builder()
                                        .title("Quantum Computing in 2023")
                                        .description("This presentation will cover the latest advancements in quantum computing.")
                                        .startTime(Instant.parse("2023-04-01T09:00:00Z"))
                                        .endTime(Instant.parse("2023-04-01T10:30:00Z"))
                                        .presenters(
                                                List.of(
                                                        Presenter.builder()
                                                                .name("Dr. Alice")
                                                                .surname("Smith")
                                                                .email("denis.klebanovich@gmail.com")
                                                                .orcid("0009-0005-9044-6202")
                                                                .isSpeaker(true)
                                                                .build(),
                                                        Presenter.builder()
                                                                .name("Dr. Bob")
                                                                .surname("Johnson")
                                                                .orcid("0000-0002-2345-6789")
                                                                .isSpeaker(false)
                                                                .build()
                                                )).build()))
                        .createdAt(Instant.parse("2023-05-01T10:30:00Z"))
                        .status(ApplicationStatus.PENDING)
                        .edition(edition)
                        .build(),

                Session.builder()
                        .title("Artificial Intelligence in Healthcare")
                        .description("This session will cover the latest trends in AI in healthcare.")
                        .creator(users.get(1))
                        .type(SessionType.TUTORIAL)
                        .presentations(List.of(
                                Presentation.builder()
                                        .title("AI in Diagnostics")
                                        .description("This presentation will cover the use of AI in medical diagnostics.")
                                        .startTime(Instant.parse("2023-09-15T15:00:00Z"))
                                        .endTime(Instant.parse("2023-09-15T16:30:00Z"))
                                        .presenters(List.of(
                                                Presenter.builder()
                                                        .name("Prof. Charles")
                                                        .surname("Lee")
                                                        .orcid("0000-0003-2345-6789")
                                                        .isSpeaker(true)
                                                        .build()
                                        )).build()))
                        .createdAt(Instant.parse("2023-09-15T15:00:00Z"))
                        .status(ApplicationStatus.CHANGE_REQUESTED)
                        .edition(edition)
                        .build(),

                Session.builder()
                        .title("Sustainability Practices in Urban Development")
                        .description("This session will cover the latest sustainability practices in urban development.")
                        .creator(users.get(2))
                        .type(SessionType.TUTORIAL)
                        .presentations(
                                List.of(Presentation.builder()
                                        .title("Green Building Design")
                                        .description("This presentation will cover the principles of green building design.")
                                        .startTime(Instant.parse("2023-11-10T12:00:00Z"))
                                        .endTime(Instant.parse("2023-11-10T13:30:00Z"))
                                        .presenters(
                                                List.of(
                                                        Presenter.builder()
                                                                .name("Dr. Emma")
                                                                .surname("Brown")
                                                                .orcid("0000-0004-2345-6789")
                                                                .isSpeaker(true)
                                                                .build(),
                                                        Presenter.builder()
                                                                .name("Mr. David")
                                                                .surname("Green")
                                                                .orcid("0000-0005-2345-6789")
                                                                .isSpeaker(false)
                                                                .build()
                                                )).build()))
                        .createdAt(Instant.parse("2023-11-10T12:00:00Z"))
                        .status(ApplicationStatus.DRAFT)
                        .edition(edition)
                        .build(),

                Session.builder()
                        .title("The Future of Renewable Energy")
                        .description("This session will cover the latest trends in renewable energy.")
                        .creator(users.get(3))
                        .type(SessionType.TUTORIAL)
                        .presentations(List.of(
                                Presentation.builder()
                                        .title("Solar Energy Innovations")
                                        .description("This presentation will cover the latest innovations in solar energy.")
                                        .startTime(Instant.parse("2024-02-20T09:00:00Z"))
                                        .endTime(Instant.parse("2024-02-20T10:30:00Z"))
                                        .presenters(List.of(
                                                Presenter.builder()
                                                        .name("Dzianis")
                                                        .surname("Klebanovich")
                                                        .orcid("0009-0005-9044-6202")
                                                        .isSpeaker(true)
                                                        .build()
                                        ))
                                        .build(),
                                Presentation.builder()
                                        .title("Wind Energy Advances")
                                        .description("This presentation will explore recent advances in wind turbine technology.")
                                        .startTime(Instant.parse("2024-02-20T10:45:00Z"))
                                        .endTime(Instant.parse("2024-02-20T12:15:00Z"))
                                        .presenters(List.of(
                                                Presenter.builder()
                                                        .name("Dr. Sophia")
                                                        .surname("Blue")
                                                        .orcid("0000-0006-5678-1234")
                                                        .isSpeaker(true)
                                                        .build()
                                        ))
                                        .build(),
                                Presentation.builder()
                                        .title("Geothermal Energy Potential")
                                        .description("This presentation will cover the untapped potential of geothermal energy for power generation.")
                                        .startTime(Instant.parse("2024-02-20T13:00:00Z"))
                                        .endTime(Instant.parse("2024-02-20T14:30:00Z"))
                                        .presenters(List.of(
                                                Presenter.builder()
                                                        .name("Dr. Olivia")
                                                        .surname("Green")
                                                        .orcid("0000-0006-7890-1234")
                                                        .isSpeaker(true)
                                                        .build()
                                        ))
                                        .build(),
                                Presentation.builder()
                                        .title("Hydropower in the Modern Era")
                                        .description("This presentation will highlight advances in hydropower technologies and applications.")
                                        .startTime(Instant.parse("2024-02-20T15:00:00Z"))
                                        .endTime(Instant.parse("2024-02-20T16:30:00Z"))
                                        .presenters(List.of(
                                                Presenter.builder()
                                                        .name("Dr. William")
                                                        .surname("Black")
                                                        .orcid("0000-0006-2345-5678")
                                                        .isSpeaker(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .createdAt(Instant.parse("2024-02-20T09:00:00Z"))
                        .status(ApplicationStatus.ACCEPTED)
                        .tags(List.of("renewable energy", "solar energy", "innovations", "sustainability"))
                        .edition(edition)
                        .build(),

                Session.builder()
                        .title("Advancements in Space Exploration")
                        .description("This session will cover the latest advancements in space exploration.")
                        .creator(users.get(4))
                        .type(SessionType.TUTORIAL)
                        .presentations(
                                List.of(
                                        Presentation.builder()
                                                .title("Mars Rover Missions")
                                                .description("This presentation will cover the latest Mars rover missions.")
                                                .startTime(Instant.parse("2024-06-15T14:00:00Z"))
                                                .endTime(Instant.parse("2024-06-15T15:30:00Z"))
                                                .presenters(
                                                        List.of(
                                                                Presenter.builder()
                                                                        .name("Dr. Grace")
                                                                        .surname("Black")
                                                                        .orcid("0000-0007-2345-6789")
                                                                        .isSpeaker(true)
                                                                        .build(),
                                                                Presenter.builder()
                                                                        .name("Dr. Henry")
                                                                        .surname("Gold")
                                                                        .orcid("0000-0008-2345-6789")
                                                                        .isSpeaker(false)
                                                                        .build()
                                                        )).build()))
                        .createdAt(Instant.parse("2024-06-15T14:00:00Z"))
                        .status(ApplicationStatus.REJECTED)
                        .edition(edition)
                        .build(),

                Session.builder()
                        .title("Cybersecurity Trends in 2024")
                        .description("This session will cover the latest trends in cybersecurity.")
                        .creator(users.get(0))
                        .type(SessionType.SESSION)
                        .presentations(List.of(
                                Presentation.builder()
                                        .title("Zero Trust Security")
                                        .description("This presentation will cover the principles of zero trust security.")
                                        .startTime(Instant.parse("2024-08-05T15:00:00Z"))
                                        .endTime(Instant.parse("2024-08-05T16:30:00Z"))
                                        .presenters(List.of(
                                                Presenter.builder()
                                                        .name("Dr. Iris")
                                                        .surname("Gray")
                                                        .orcid("0000-0009-2345-6789")
                                                        .isSpeaker(true)
                                                        .build()
                                        )).build()))
                        .createdAt(Instant.parse("2024-08-05T16:30:00Z"))
                        .status(ApplicationStatus.PENDING)
                        .edition(edition)
                        .build()
        );
        sessions.forEach(session -> {
            session.getPresentations().forEach(presentation -> {
                presentation.setSession(session);
                presentation.getPresenters().forEach(presenter -> {
                    presenter.setPresentation(presentation);
                    presenter.setEmail(presenter.getName().toLowerCase() + "." + presenter.getSurname().toLowerCase() + "@gmail.com");
                });
            });
        });
        return sessionRepository.saveAll(sessions);
    }
}
