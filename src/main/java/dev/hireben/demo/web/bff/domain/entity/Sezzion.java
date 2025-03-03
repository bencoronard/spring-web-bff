package dev.hireben.demo.web.bff.domain.entity;

import java.time.Instant;

import dev.hireben.demo.web.bff.domain.model.Uzer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sezzion {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final String id;
  private final Uzer user;
  private final String syncToken;
  private final Instant createdAt;
  private Instant expiresAt;

}
