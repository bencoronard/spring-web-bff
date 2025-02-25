package dev.hireben.demo.rest.web.bff.application.mapper;

import dev.hireben.demo.rest.web.bff.application.dto.UserDetailsDTO;
import dev.hireben.demo.rest.web.bff.domain.model.UserDetails;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserDetailsMapper {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserDetailsDTO toDto(UserDetails userInfo) {
    return UserDetailsDTO.builder()
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
