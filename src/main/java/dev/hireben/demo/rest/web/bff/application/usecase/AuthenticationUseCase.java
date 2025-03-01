package dev.hireben.demo.rest.web.bff.application.usecase;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import dev.hireben.demo.rest.web.bff.application.dto.AuthenticationDTO;
import dev.hireben.demo.rest.web.bff.application.dto.SezzionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.InvalidSyncTokenException;
import dev.hireben.demo.rest.web.bff.application.exception.SessionExpiredException;
import dev.hireben.demo.rest.web.bff.application.exception.SessionNotFoundException;
import dev.hireben.demo.rest.web.bff.application.exception.DuplicateAuthenticationException;
import dev.hireben.demo.rest.web.bff.application.exception.AuthenticationFailedException;
import dev.hireben.demo.rest.web.bff.application.mapper.SezzionMapper;
import dev.hireben.demo.rest.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.rest.web.bff.application.service.SyncTokenService;
import dev.hireben.demo.rest.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.rest.web.bff.domain.model.Uzer;
import dev.hireben.demo.rest.web.bff.domain.repository.SezzionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationUseCase {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final int MAX_ACTIVE_USER_SESSIONS;
  private final int USER_SESSION_TTL_IN_SEC;

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final SezzionRepository sessionRepository;
  private final AuthenticationService authenticationService;
  private final SyncTokenService syncTokenService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public SezzionDTO authenticateSession(String sessionId, AuthenticationDTO dto) {

    Sezzion existingSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authenticate non-existent session %s", sessionId)));

    if (existingSession.getExpiresAt().isBefore(Instant.now())) {
      throw new SessionExpiredException(String.format("Failed to authenticate expired session %s", sessionId));
    }

    if (existingSession.getUser() != null) {
      throw new DuplicateAuthenticationException(
          String.format("Failed to authenticate already authenticated session %s owned by user %s", sessionId,
              existingSession.getUser().getId()));
    }

    Uzer user = authenticationService.authenticate(dto.getUsername(), dto.getPassword())
        .orElseThrow(() -> new AuthenticationFailedException(
            String.format("Failed to authenticate anonymous session %s", sessionId)));

    Collection<Sezzion> activeSessions = sessionRepository.findAllByUserId(user.getId());

    if (activeSessions.size() >= MAX_ACTIVE_USER_SESSIONS) {
      Optional<Sezzion> oldestSession = activeSessions.stream()
          .min(Comparator.comparing(Sezzion::getCreatedAt));
      oldestSession.ifPresent(session -> sessionRepository.deleteById(session.getId()));
    }

    sessionRepository.deleteById(sessionId);

    Instant now = Instant.now();
    Sezzion newSession = Sezzion.builder()
        .user(user)
        .syncToken(syncTokenService.generate())
        .createdAt(now)
        .expiresAt(now.plusSeconds(USER_SESSION_TTL_IN_SEC))
        .build();

    return SezzionMapper.toDto(sessionRepository.save(newSession));
  }

  // ---------------------------------------------------------------------------//

  public void invalidateSession(String sessionId, String syncToken) {
    sessionRepository.findById(sessionId).ifPresent(session -> {

      if (!session.getSyncToken().equals(syncToken)) {
        throw new InvalidSyncTokenException(
            String.format("Failed to invalidate session %s due to invalid CSRF token", sessionId));
      }

      sessionRepository.deleteById(sessionId);
    });
  }

}
