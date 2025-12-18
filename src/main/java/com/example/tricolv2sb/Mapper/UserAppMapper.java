package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.Entity.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAppMapper {

    @Mapping(target = "roleName", source = "role.name")
    ReadUserDTO toReadUserDTO(UserApp userApp);

    List<ReadUserDTO> toReadUserDTOList(List<UserApp> userApps);
}
