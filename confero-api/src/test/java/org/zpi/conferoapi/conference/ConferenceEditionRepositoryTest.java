package org.zpi.conferoapi.conference;

import org.junit.jupiter.api.Test;
import org.zpi.conferoapi.DataJpaTestBase;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConferenceEditionRepositoryTest extends DataJpaTestBase {

    @Test
    void findActiveEditionConference() {
        var now = Instant.now();

        var conferenceEdition = ConferenceEdition.builder()
                .id(1L)
                .applicationDeadlineTime(now.plus(1, DAYS))
                .createdAt(now)
                .build();

        conferenceEditionRepository.save(conferenceEdition);

        var activeEditionConference = conferenceEditionRepository.findActiveEditionConference();
        assertTrue(activeEditionConference.isPresent());
    }


    @Test
    void doNotFindActiveEditionConference() {
        var now = Instant.now();

        var conferenceEdition = ConferenceEdition.builder()
                .id(1L)
                .applicationDeadlineTime(now.minus(1, DAYS))
                .createdAt(now.minus(2, DAYS))
                .build();

        conferenceEditionRepository.save(conferenceEdition);

        var activeEditionConference = conferenceEditionRepository.findActiveEditionConference();
        assertTrue(activeEditionConference.isEmpty());
    }

}