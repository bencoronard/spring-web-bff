package dev.hireben.demo.rest.web.bff.application.usecase;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import dev.hireben.demo.rest.web.bff.application.dto.AuthenticationDTO;
import dev.hireben.demo.rest.web.bff.application.dto.UserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.InvalidCsrfTokenException;
import dev.hireben.demo.rest.web.bff.application.exception.UserAuthenticationException;
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

  public UserSessionDTO authenticate(AuthenticationDTO dto) {

    UserDetails user = authenticationService.authenticate(dto.getUsername(), dto.getPassword())
        .orElseThrow(() -> new UserAuthenticationException("Invalid credentials"));

    Collection<UserSession> activeSessions = sessionRepository.findAllByUserId(user.getId());

    if (activeSessions.size() >= MAX_ACTIVE_USER_SESSIONS) {

      Optional<UserSession> oldestSession = activeSessions.stream()
          .min(Comparator.comparing(UserSession::getCreatedAt));

      oldestSession.ifPresent(session -> sessionRepository.deleteById(session.getId()));
    }

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

  public void deauthenticate(String sessionId, String csrfToken) {

    sessionRepository.findById(sessionId).ifPresent(session -> {

      if (!session.getCsrfToken().equals(csrfToken)) {
        throw new InvalidCsrfTokenException("Invalid CSRF token");
      }

      sessionRepository.deleteById(sessionId);
    });

  }

}
