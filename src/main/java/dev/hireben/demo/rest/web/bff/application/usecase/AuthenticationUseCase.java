package dev.hireben.demo.rest.web.bff.application.usecase;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import dev.hireben.demo.rest.web.bff.application.dto.AuthenticationDTO;
import dev.hireben.demo.rest.web.bff.application.dto.SezzionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.InvalidCsrfTokenException;
import dev.hireben.demo.rest.web.bff.application.exception.SessionNotFoundException;
import dev.hireben.demo.rest.web.bff.application.exception.DeniedAccessException;
import dev.hireben.demo.rest.web.bff.application.exception.FailedAuthenticationException;
import dev.hireben.demo.rest.web.bff.application.mapper.SezzionMapper;
import dev.hireben.demo.rest.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.rest.web.bff.application.service.CsrfTokenService;
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
  private final CsrfTokenService csrfTokenService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public SezzionDTO authenticateSession(String sessionId, AuthenticationDTO dto) {

    Sezzion anonymousSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authenticate non-existent session %s", sessionId)));

    if (anonymousSession.getUser() != null) {
      throw new DeniedAccessException(
          String.format("Failed to authenticate already authenticated session %s owned by user %s", sessionId,
              anonymousSession.getUser().getId()));
    }

    Uzer user = authenticationService.authenticate(dto.getUsername(), dto.getPassword())
        .orElseThrow(() -> new FailedAuthenticationException(
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
        .csrfToken(csrfTokenService.generate())
        .createdAt(now)
        .expiresAt(now.plusSeconds(USER_SESSION_TTL_IN_SEC))
        .build();

    return SezzionMapper.toDto(sessionRepository.save(newSession));
  }

  // ---------------------------------------------------------------------------//

  public void invalidateSession(String sessionId, String csrfToken) {

    sessionRepository.findById(sessionId).ifPresent(session -> {

      if (!session.getCsrfToken().equals(csrfToken)) {
        throw new InvalidCsrfTokenException(
            String.format("Failed to invalidate session %s due to invalid CSRF token", sessionId));
      }

      sessionRepository.deleteById(sessionId);
    });

  }

}
