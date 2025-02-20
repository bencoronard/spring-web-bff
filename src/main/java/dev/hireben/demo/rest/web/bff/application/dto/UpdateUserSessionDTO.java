package dev.hireben.demo.rest.web.bff.application.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateUserSessionDTO {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final Instant expiresAt;

}
