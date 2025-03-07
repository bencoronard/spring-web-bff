package dev.hireben.demo.web.bff.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Uzer {

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
