package dev.hireben.demo.rest.web.bff.application.usecase;

import java.util.Collection;

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

  public Collection<String> authorizeView(String sessionId, String permissionId) {

    UserSession existingSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    return permissionService.hasViewPermission(existingSession.getUser().getRoleId(), permissionId);
  }

  // ---------------------------------------------------------------------------//

  public boolean authorizeApi(String sessionId, String permissionId) {

    UserSession existingSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    return permissionService.hasApiPermission(existingSession.getUser().getRoleId(), permissionId);
  }

}
