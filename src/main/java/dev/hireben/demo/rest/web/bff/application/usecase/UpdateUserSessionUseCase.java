package dev.hireben.demo.rest.web.bff.application.usecase;

import java.util.Optional;

import dev.hireben.demo.rest.web.bff.application.dto.UpdateUserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.dto.UserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.UserSessionNotFound;
import dev.hireben.demo.rest.web.bff.application.mapper.UserSessionMapper;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUserSessionUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final UserSessionRepository repository;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserSessionDTO updateSession(String id, UpdateUserSessionDTO dto) {

    UserSession foundSession = repository.findById(id)
        .orElseThrow(() -> new UserSessionNotFound(String.format("Session %s not found", id)));

    Optional.ofNullable(dto.getExpiresAt()).ifPresent(foundSession::setExpiresAt);

    UserSession updatedSession = repository.save(foundSession);

    return UserSessionMapper.toDto(updatedSession);
  }

}
