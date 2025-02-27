package dev.hireben.demo.rest.web.bff.application.usecase;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import dev.hireben.demo.rest.web.bff.application.dto.SezzionDTO;
import dev.hireben.demo.rest.web.bff.application.exception.RedirectToProtectedException;
import dev.hireben.demo.rest.web.bff.application.exception.SessionNotFoundException;
import dev.hireben.demo.rest.web.bff.application.exception.DeniedAccessException;
import dev.hireben.demo.rest.web.bff.application.exception.DeniedPermissionException;
import dev.hireben.demo.rest.web.bff.application.exception.InvalidCsrfTokenException;
import dev.hireben.demo.rest.web.bff.application.mapper.SezzionMapper;
import dev.hireben.demo.rest.web.bff.application.service.PermissionService;
import dev.hireben.demo.rest.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.rest.web.bff.domain.repository.SezzionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizationUseCase {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final int ANONYMOUS_SESSION_TTL_IN_SEC;

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final SezzionRepository sessionRepository;
  private final PermissionService permissionService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizePublicViewAccess(String sessionId) {

    if (sessionId != null) {
      Optional<Sezzion> existingSession = sessionRepository.findById(sessionId);

      if (existingSession.isPresent()) {
        Sezzion anonymousSession = existingSession.get();

        if (anonymousSession.getUser() != null) {
          throw new RedirectToProtectedException(
              String.format("Failed to authorize public view access by authenticated session %s", sessionId));
        }

        return SezzionMapper.toDto(anonymousSession);
      }
    }

    Instant now = Instant.now();
    Sezzion newAnonymousSession = Sezzion.builder()
        .createdAt(now)
        .expiresAt(now.plusSeconds(ANONYMOUS_SESSION_TTL_IN_SEC))
        .build();

    return SezzionMapper.toDto(sessionRepository.save(newAnonymousSession));
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizeViewAccess(String sessionId, String permissionId) {

    Sezzion activeSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authorize protected view access by non-existent session %s", sessionId)));

    if (activeSession.getUser() == null) {
      throw new DeniedAccessException(
          String.format("Failed to authorize protected view access (permission %s) by anonymous session %s",
              permissionId, sessionId));
    }

    Collection<String> viewPermissionTokens = permissionService.hasViewPermission(activeSession.getUser().getRoleId(),
        permissionId).orElseThrow(
            () -> new DeniedPermissionException(
                String.format("Failed to authorize protected view access (permission %s) by user %s", permissionId,
                    activeSession.getUser().getId())));

    return SezzionMapper.toDtoWithTokens(activeSession, viewPermissionTokens);
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizeApiAccess(String sessionId, String permissionId) {

    Sezzion activeSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authorize API access by non-existent session %s", sessionId)));

    if (activeSession.getUser() == null) {
      throw new DeniedAccessException(
          String.format("Failed to authorize API access (permission %s) by anonymous session %s",
              permissionId, sessionId));
    }

    if (!permissionService.hasApiPermission(activeSession.getUser().getRoleId(), permissionId)) {
      throw new DeniedPermissionException(
          String.format("Failed to authorize API access (permission %s) by user %s", permissionId,
              activeSession.getUser().getId()));
    }

    return SezzionMapper.toDto(activeSession);
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizeStateChangingApiAccess(String sessionId, String permissionId, String syncToken) {

    Sezzion activeSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authorize API access by non-existent session %s", sessionId)));

    if (activeSession.getUser() == null) {
      throw new DeniedAccessException(
          String.format("Failed to authorize API access (permission %s) by anonymous session %s",
              permissionId, sessionId));
    }

    if (activeSession.getSyncToken() == null || !activeSession.getSyncToken().equals(syncToken)) {
      throw new InvalidCsrfTokenException(String.format(
          "Failed to authorize API access (permission %s) by user %s due to missing or invalid synchronizer token",
          permissionId, activeSession.getUser().getId()));
    }

    if (!permissionService.hasApiPermission(activeSession.getUser().getRoleId(), permissionId)) {
      throw new DeniedPermissionException(
          String.format("Failed to authorize API access (permission %s) by user %s", permissionId,
              activeSession.getUser().getId()));
    }

    return SezzionMapper.toDto(activeSession);
  }

}
