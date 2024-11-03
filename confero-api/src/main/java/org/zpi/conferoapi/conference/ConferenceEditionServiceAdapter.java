package org.zpi.conferoapi.conference;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.openapitools.model.CreateConferenceEditionRequest;
import org.openapitools.model.ErrorReason;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.user.UserRepository;
import org.zpi.conferoapi.util.CsvReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import org.zpi.conferoapi.user.User;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ConferenceEditionServiceAdapter implements ConferenceEditionService {

    ConferenceEditionRepository conferenceEditionRepository;
    UserRepository userRepository;

    @Override
    public ConferenceEdition createConferenceEdition(CreateConferenceEdition createConferenceEditionRequest) {
        var activeEdition = conferenceEditionRepository.findActiveEditionConference();

        if (activeEdition.isPresent()) {
            throw new ServiceException(ErrorReason.ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS);
        }

        var invitees = new ArrayList<User>();

        if(createConferenceEditionRequest.getInvitationList() != null) {
            log.info("Invitation list is provided: {}", createConferenceEditionRequest.getInvitationList().getName());
            var emails = CsvReader.readEmailList(createConferenceEditionRequest.getInvitationList());
            log.info("Read {} emails from the invitation list", emails);

            emails.forEach(email -> {
                var user = userRepository.findByEmail(email);
                if (user.isEmpty()) {
                    log.info("User with email {} does not exist, creating new user", email);
                    var saved = userRepository.save(User.builder()
                            .email(email)
                            .isAdmin(false)
                            .build());
                    invitees.add(saved);
                } else {
                    log.info("User with email {} already exists", email);
                    invitees.add(user.get());
                }
            });
        }

        return conferenceEditionRepository.save(ConferenceEdition.builder()
                .applicationDeadlineTime(createConferenceEditionRequest.getApplicationDeadlineTime())
                .createdAt(Instant.now())
                .invitees(invitees)
                .build());
    }
}
