package dev.hireben.demo.rest.web.bff.application.dto;

import java.time.Instant;
import java.util.Collection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SezzionDTO {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  String sessionId;
  String syncToken;
  Instant expiresAt;
  Collection<String> permissionTokens;

}
