package org.zpi.conferoapi.session;

import org.mapstruct.*;
import org.openapitools.model.SessionPreviewResponse;
import org.openapitools.model.SessionResponse;

import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SessionMapper {

    SessionResponse toDto(Session session);

    SessionPreviewResponse toPreviewDto(Session session);

    static List<String> tagsToString(Map<String, Object> tags) {
        return tags.keySet().stream().toList();
    }
}