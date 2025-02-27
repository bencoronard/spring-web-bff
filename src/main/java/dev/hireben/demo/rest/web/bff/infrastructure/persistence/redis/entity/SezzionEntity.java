package dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.entity;

import java.time.Instant;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@RedisHash("portal:sezzion")
public class SezzionEntity {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private String id;
  private String syncToken;
  private final Instant createdAt;

}
