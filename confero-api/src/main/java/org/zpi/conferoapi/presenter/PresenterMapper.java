package org.zpi.conferoapi.presenter;

import org.mapstruct.*;
import org.zpi.conferoapi.proposal.Presenter;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PresenterMapper {
    Presenter toEntity(PresenterDto presenterDto);

    PresenterDto toDto(Presenter presenter);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Presenter partialUpdate(PresenterDto presenterDto, @MappingTarget Presenter presenter);
}