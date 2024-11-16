package org.zpi.conferoapi.user;

import org.mapstruct.*;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.SessionResponse;
import org.zpi.conferoapi.session.Session;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", source = "surname")
    ProfileResponse toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);


    default ProfileResponse toDto(User user, Boolean isInvitee ) {
        ProfileResponse profileResponse = toDto(user);
        profileResponse.setIsInvitee(isInvitee);
        return profileResponse;
    }
}