package org.zpi.conferoapi.organizer;


import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.OrganizerResponse;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.orcid.OrcidService;

import java.util.List;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class OrganizerService {

    OrganizerRepository organizerRepository;
    OrcidService orcidService;


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

    public List<OrganizerResponse> massUpdateOrganizers() {
        var organizers = organizerRepository.findAll();
        return organizers.stream()
                .map(organizer -> {
                    var info = orcidService.getRecord(organizer.getOrcid());
                    organizer.setName(info.getName());
                    organizer.setSurname(info.getSurname());
                    organizer.setOrganization(info.getOrganization());
                    organizer.setTitle(info.getTitle());
                    return organizerRepository.save(organizer);
                })
                .map(organizer -> new OrganizerResponse()
                        .id(organizer.getId())
                        .name(organizer.getName())
                        .surname(organizer.getSurname())
                        .orcid(organizer.getOrcid())
                )
                .toList();
    }

}
