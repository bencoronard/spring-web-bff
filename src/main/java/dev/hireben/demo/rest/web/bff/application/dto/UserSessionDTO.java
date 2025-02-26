package dev.hireben.demo.rest.web.bff.application.dto;

import java.time.Instant;
import java.util.Collection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserSessionDTO {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  String sessionId;
  String csrfToken;
  Instant expiresAt;
  Collection<String> permissionTokens;

}
