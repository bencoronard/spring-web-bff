package dev.hireben.demo.web.bff.infrastructure.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.hireben.demo.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.web.bff.domain.model.Uzer;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final Collection<Uzer> MOCK_USERS = List.of(
      Uzer.builder()
          .id("1")
          .firstName("Jira")
          .lastName("555")
          .badgeNum("tang-dev-555")
          .position("Software Developer")
          .roleId("DEV-1")
          .tenant("CONSENT")
          .build(),
      Uzer.builder()
          .id("2")
          .firstName("Kevin")
          .lastName("888")
          .badgeNum("k-dev-888")
          .position("Software Engineer")
          .roleId("DEV-2")
          .tenant("NOTIFICATION")
          .build(),
      Uzer.builder()
          .id("3")
          .firstName("MamMOSFET")
          .lastName("666")
          .badgeNum("mothz-dev-666")
          .position("System Engineer")
          .roleId("DEV-3")
          .tenant("INSURANCE")
          .build(),
      Uzer.builder()
          .id("4")
          .firstName("Thirdzaza")
          .lastName("111")
          .badgeNum("third-dev-111")
          .position("Database Administrator")
          .roleId("DBA-1")
          .tenant("AUCTION")
          .build());

  private final String MOCK_PASSWORD = "P@ssw0rd";

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public Optional<Uzer> authenticate(String username, String password) {
    return MOCK_USERS.stream()
        .filter(user -> (user.getFirstName() + user.getLastName()).equalsIgnoreCase(username))
        .filter(_ -> MOCK_PASSWORD.equals(password))
        .findFirst();
  }

}
