package dev.hireben.demo.rest.web.bff.application.mapper;

import dev.hireben.demo.rest.web.bff.application.dto.UserDTO;
import dev.hireben.demo.rest.web.bff.domain.model.UserInfo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserInfoMapper {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserDTO toDto(UserInfo userInfo) {
    return UserDTO.builder()
        .id(userInfo.getId())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .badgeNum(userInfo.getBadgeNum())
        .position(userInfo.getPosition())
        .roleId(userInfo.getRoleId())
        .tenant(userInfo.getTenant())
        .build();
  }

}
