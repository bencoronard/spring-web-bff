package dev.hireben.demo.rest.web.bff.application.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDTO {

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
