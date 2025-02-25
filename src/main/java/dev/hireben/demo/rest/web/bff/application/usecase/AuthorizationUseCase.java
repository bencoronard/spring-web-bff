package dev.hireben.demo.rest.web.bff.application.usecase;

import java.util.Collection;

import dev.hireben.demo.rest.web.bff.application.exception.AccessDeniedException;
import dev.hireben.demo.rest.web.bff.application.exception.InvalidCsrfTokenException;
import dev.hireben.demo.rest.web.bff.application.exception.PermissionDeniedException;
import dev.hireben.demo.rest.web.bff.application.exception.SessionNotFoundException;
import dev.hireben.demo.rest.web.bff.application.service.PermissionService;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizationUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final UserSessionRepository sessionRepository;
  private final PermissionService permissionService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public void authorizePublicViewAccess(String sessionId) {

    if (sessionId == null) {
      return;
    }

    sessionRepository.findById(sessionId).ifPresent(_ -> {
      throw new AccessDeniedException("User already authenticated");
    });
  }

  // ---------------------------------------------------------------------------//

  public Collection<String> authorizeViewAccess(String sessionId, String permissionId) {

    UserSession activeSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    Collection<String> subViewPermissions = permissionService.hasViewPermission(activeSession.getUser().getRoleId(),
        permissionId);

    if (subViewPermissions == null) {
      throw new PermissionDeniedException("User not authorized");
    }

    return subViewPermissions;
  }

  // ---------------------------------------------------------------------------//

  public void authorizeApiAccess(String sessionId, String permissionId) {

    UserSession activeSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    if (!permissionService.hasApiPermission(activeSession.getUser().getRoleId(), permissionId)) {
      throw new PermissionDeniedException("User not authorized");
    }
  }

  // ---------------------------------------------------------------------------//

  public void authorizeStateChangingApiAccess(String sessionId, String permissionId, String csrfToken) {

    UserSession activeSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    if (!activeSession.getCsrfToken().equals(csrfToken)) {
      throw new InvalidCsrfTokenException("Invalid CSRF token");
    }

    if (!permissionService.hasApiPermission(activeSession.getUser().getRoleId(), permissionId)) {
      throw new PermissionDeniedException("User not authorized");
    }
  }

}
