package dev.hireben.demo.rest.web.bff.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDTO {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final String id;
  private final String badgeId;
  private final String firstName;
  private final String lastName;
  private final String tenant;
  private final String position;
  private final String roleId;

}
