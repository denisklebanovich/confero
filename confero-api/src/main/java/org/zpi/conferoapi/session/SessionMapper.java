package org.zpi.conferoapi.session;

import org.mapstruct.*;
import org.zpi.conferoapi.presenter.PresenterMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PresenterMapper.class, SessionMapper.class})
public interface SessionMapper {
    Attachment toEntity(AttachmentDto attachmentDto);

    AttachmentDto toDto(Attachment attachment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Attachment partialUpdate(AttachmentDto attachmentDto, @MappingTarget Attachment attachment);

    Session toEntity(SessionDto sessionDto);

    SessionDto toDto(Session session);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Session partialUpdate(SessionDto sessionDto, @MappingTarget Session session);
}