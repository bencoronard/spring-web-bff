package dev.hireben.demo.web.bff.infrastructure.persistence.redis.mapper;

import java.time.Instant;
import dev.hireben.demo.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.web.bff.infrastructure.persistence.redis.entity.SezzionEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SezzionEntityMapper {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public SezzionEntity fromDomain(Sezzion session) {
    return SezzionEntity.builder()
        .id(session.getId())
        .user(session.getUser())
        .syncToken(session.getSyncToken())
        .createdAt(session.getCreatedAt())
        .build();
  }

  // ---------------------------------------------------------------------------//

  public Sezzion toDomain(SezzionEntity entity, Instant expiresAt) {
    return Sezzion.builder()
        .id(entity.getId())
        .user(entity.getUser())
        .syncToken(entity.getSyncToken())
        .createdAt(entity.getCreatedAt())
        .expiresAt(expiresAt)
        .build();
  }

}
