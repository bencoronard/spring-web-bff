package dev.hireben.demo.rest.web.bff.application.usecase;

import dev.hireben.demo.rest.web.bff.application.dto.UserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.UserSessionNotFound;
import dev.hireben.demo.rest.web.bff.application.mapper.UserSessionMapper;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RetrieveUserSessionUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final UserSessionRepository repository;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserSessionDTO findSession(String id) {

    UserSession foundSession = repository.findById(id)
        .orElseThrow(() -> new UserSessionNotFound(String.format("Session %s not found", id)));

    return UserSessionMapper.toDto(foundSession);
  }

}
