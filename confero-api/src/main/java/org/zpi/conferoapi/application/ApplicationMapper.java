package org.zpi.conferoapi.application;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.ApplicationResponse;
import org.zpi.conferoapi.session.Session;

import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationMapper {

    ApplicationResponse sessionToApplicationResponse(Session session);

    static List<String> tagsToString(Map<String, Object> tags) {
        return tags.keySet().stream().toList();
    }
}