package dev.hireben.demo.rest.web.bff.application.mapper;

import java.util.Collection;

import dev.hireben.demo.rest.web.bff.application.dto.SezzionDTO;
import dev.hireben.demo.rest.web.bff.domain.entity.Sezzion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SezzionMapper {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public SezzionDTO toDto(Sezzion session) {
    return SezzionDTO.builder()
        .sessionId(session.getId())
        .csrfToken(session.getSyncToken())
        .expiresAt(session.getExpiresAt())
        .build();
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO toDtoWithTokens(Sezzion session, Collection<String> permissionTokens) {
    return SezzionDTO.builder()
        .sessionId(session.getId())
        .permissionTokens(permissionTokens)
        .csrfToken(session.getSyncToken())
        .expiresAt(session.getExpiresAt())
        .build();
  }

}
