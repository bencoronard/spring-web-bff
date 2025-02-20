package dev.hireben.demo.rest.web.bff.domain.entity;

import java.time.Instant;

import dev.hireben.demo.rest.web.bff.domain.model.UserInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSession {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final String id;
  private final UserInfo user;
  private final Instant createdAt;
  private Instant expiresAt;

}
