package dev.hireben.demo.rest.web.bff.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDetails {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  String id;
  String firstName;
  String lastName;
  String badgeNum;
  String position;
  String roleId;
  String tenant;

}
