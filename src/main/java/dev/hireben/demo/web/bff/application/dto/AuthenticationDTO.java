package dev.hireben.demo.web.bff.application.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthenticationDTO {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  String username;
  String password;

}
