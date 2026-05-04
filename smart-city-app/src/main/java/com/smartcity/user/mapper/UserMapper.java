package com.smartcity.user.mapper;

import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toDto(User user);
}
