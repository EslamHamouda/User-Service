package com.ecommerce.userservice.mapper;

import com.ecommerce.userservice.entity.UserEntity;
import com.ecommerce.userservice.dto.response.LoginDtoResponse;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "type", constant = "Bearer")
    LoginDtoResponse toLoginResponse(UserEntity entity, String accessToken, String refreshToken);

    ProfileDtoResponse toProfileResponse(UserEntity entity);
}
