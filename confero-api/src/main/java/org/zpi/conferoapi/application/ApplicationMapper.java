package org.zpi.conferoapi.application;

import org.mapstruct.*;
import org.openapitools.model.ApplicationPreviewResponse;
import org.openapitools.model.ApplicationResponse;
import org.openapitools.model.PresenterResponse;
import org.openapitools.model.UpdateApplicationRequest;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.session.Session;

import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationMapper {

    ApplicationResponse sessionToApplicationResponse(Session session);

    @Mapping(target = "presenters", source = "presentations", qualifiedByName = "mapPresentersFromPresentations")
    ApplicationPreviewResponse toPreviewDto(Session session);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "presentations", ignore = true)
    Session partialUpdate(UpdateApplicationRequest request, @MappingTarget Session session);

    PresenterResponse toDto(Presenter presenter);

    @Named("mapPresentersFromPresentations")
    default List<PresenterResponse> mapPresentersFromPresentations(List<Presentation> presentations) {
        return presentations.stream()
                .flatMap(presentation -> presentation.getPresenters().stream())
                .map(this::toDto)
                .toList();
    }

}