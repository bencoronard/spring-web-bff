package dev.hireben.demo.rest.web.bff.application.mapper;

import dev.hireben.demo.rest.web.bff.application.dto.UserSessionDTO;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserSessionMapper {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserSessionDTO toDto(UserSession session) {
    return UserSessionDTO.builder()
        .sessionId(session.getId())
        .csrfToken(session.getCsrfToken())
        .expiresAt(session.getExpiresAt())
        .build();
  }

}
