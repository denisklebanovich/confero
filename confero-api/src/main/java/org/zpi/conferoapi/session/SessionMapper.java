package org.zpi.conferoapi.session;

import org.mapstruct.*;
import org.openapitools.model.PresenterResponse;
import org.openapitools.model.SessionPreviewResponse;
import org.openapitools.model.SessionResponse;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.Presenter;

import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SessionMapper {

    SessionResponse toDto(Session session);

    @Mapping(target = "presenters", source = "presentations", qualifiedByName = "mapPresentersFromPresentations")
    SessionPreviewResponse toPreviewDto(Session session);

    static List<String> tagsToString(Map<String, Object> tags) {
        return tags.keySet().stream().toList();
    }

    PresenterResponse toDto(Presenter presenter);

    @Named("mapPresentersFromPresentations")
    default List<PresenterResponse> mapPresentersFromPresentations(List<Presentation> presentations) {
        return presentations.stream()
                .flatMap(presentation -> presentation.getPresenters().stream())
                .map(this::toDto)
                .toList();
    }
}