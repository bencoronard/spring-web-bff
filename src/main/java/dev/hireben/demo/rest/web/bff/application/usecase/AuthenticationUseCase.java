package dev.hireben.demo.rest.web.bff.application.usecase;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import dev.hireben.demo.rest.web.bff.application.dto.AuthenticationDTO;
import dev.hireben.demo.rest.web.bff.application.dto.UserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.InvalidCsrfTokenException;
import dev.hireben.demo.rest.web.bff.application.exception.SessionNotFoundException;
import dev.hireben.demo.rest.web.bff.application.exception.DeniedAccessException;
import dev.hireben.demo.rest.web.bff.application.exception.FailedAuthenticationException;
import dev.hireben.demo.rest.web.bff.application.mapper.UserSessionMapper;
import dev.hireben.demo.rest.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.rest.web.bff.application.service.CsrfTokenService;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.model.UserDetails;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
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

  private final UserSessionRepository sessionRepository;
  private final AuthenticationService authenticationService;
  private final CsrfTokenService csrfTokenService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserSessionDTO authenticateSession(String sessionId, AuthenticationDTO dto) {

    UserSession anonymousSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authenticate non-existent session %s", sessionId)));

    if (anonymousSession.getUser() != null) {
      throw new DeniedAccessException(
          String.format("Failed to authenticate already authenticated session %s owned by user %s", sessionId,
              anonymousSession.getUser().getId()));
    }

    UserDetails user = authenticationService.authenticate(dto.getUsername(), dto.getPassword())
        .orElseThrow(() -> new FailedAuthenticationException(
            String.format("Failed to authenticate anonymous session %s", sessionId)));

    Collection<UserSession> activeSessions = sessionRepository.findAllByUserId(user.getId());

    if (activeSessions.size() >= MAX_ACTIVE_USER_SESSIONS) {
      Optional<UserSession> oldestSession = activeSessions.stream()
          .min(Comparator.comparing(UserSession::getCreatedAt));

      oldestSession.ifPresent(session -> sessionRepository.deleteById(session.getId()));
    }

    sessionRepository.deleteById(sessionId);

    Instant now = Instant.now();
    UserSession newSession = UserSession.builder()
        .user(user)
        .csrfToken(csrfTokenService.generate())
        .createdAt(now)
        .expiresAt(now.plusSeconds(USER_SESSION_TTL_IN_SEC))
        .build();

    return UserSessionMapper.toDto(sessionRepository.save(newSession));
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
