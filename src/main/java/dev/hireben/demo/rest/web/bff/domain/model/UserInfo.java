package dev.hireben.demo.rest.web.bff.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {

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
