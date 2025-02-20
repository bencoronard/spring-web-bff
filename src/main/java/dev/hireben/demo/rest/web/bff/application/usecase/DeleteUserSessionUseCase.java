package dev.hireben.demo.rest.web.bff.application.usecase;

import dev.hireben.demo.rest.web.bff.application.exception.UserSessionNotFound;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUserSessionUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final UserSessionRepository repository;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public void deleteSession(String id) {

    UserSession foundSession = repository.findById(id)
        .orElseThrow(() -> new UserSessionNotFound(String.format("Failed to delete: session %s not found", id)));

    repository.delete(foundSession);
  }

}
