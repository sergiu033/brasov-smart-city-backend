package com.smartcity.user.mapper;

import com.smartcity.user.dto.UserProfileResponse;
import com.smartcity.user.entity.User;
import com.smartcity.user_vehicles.mapper.VehicleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = VehicleMapper.class
)
public interface UserMapper {
    UserProfileResponse toDto(User user);
}
