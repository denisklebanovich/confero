package org.zpi.conferoapi.organizer;


import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.OrganizerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class OrganizerService {

    OrganizerRepository organizerRepository;

    public List<OrganizerResponse> findOrganizers(String searchQuery) {
        var organizers = organizerRepository.findOrganizers(searchQuery);
        return organizers.stream()
                .map(organizer -> new OrganizerResponse()
                        .id(organizer.getId())
                        .name(organizer.getName())
                        .surname(organizer.getSurname())
                        .orcid(organizer.getOrcid())
                )
                .toList();
    }

}
