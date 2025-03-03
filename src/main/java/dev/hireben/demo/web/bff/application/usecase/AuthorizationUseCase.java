package dev.hireben.demo.web.bff.application.usecase;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import dev.hireben.demo.web.bff.application.dto.SezzionDTO;
import dev.hireben.demo.web.bff.application.exception.PublicViewAccessDeniedException;
import dev.hireben.demo.web.bff.application.exception.SessionExpiredException;
import dev.hireben.demo.web.bff.application.exception.SessionNotFoundException;
import dev.hireben.demo.web.bff.application.exception.ApiAccessDeniedException;
import dev.hireben.demo.web.bff.application.exception.ApiPermissionDeniedException;
import dev.hireben.demo.web.bff.application.exception.InvalidSyncTokenException;
import dev.hireben.demo.web.bff.application.exception.ProtectedViewAccessDeniedException;
import dev.hireben.demo.web.bff.application.exception.ProtectedViewPermissionDeniedException;
import dev.hireben.demo.web.bff.application.mapper.SezzionMapper;
import dev.hireben.demo.web.bff.application.service.PermissionService;
import dev.hireben.demo.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.web.bff.domain.repository.SezzionRepository;
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

  public Optional<SezzionDTO> authorizePublicViewAccess(String sessionId) {

    if (sessionId == null) {
      Instant now = Instant.now();
      Sezzion newAnonymousSession = Sezzion.builder()
          .createdAt(now)
          .expiresAt(now.plusSeconds(ANONYMOUS_SESSION_TTL_IN_SEC))
          .build();
      return Optional.of(SezzionMapper.toDto(sessionRepository.save(newAnonymousSession)));
    }

    Optional<Sezzion> existingSession = sessionRepository.findById(sessionId);

    if (existingSession.isEmpty() || existingSession.get().getExpiresAt().isBefore(Instant.now())) {
      Instant now = Instant.now();
      Sezzion newAnonymousSession = Sezzion.builder()
          .createdAt(now)
          .expiresAt(now.plusSeconds(ANONYMOUS_SESSION_TTL_IN_SEC))
          .build();
      return Optional.of(SezzionMapper.toDto(sessionRepository.save(newAnonymousSession)));
    }

    if (existingSession.get().getUser() != null) {
      throw new PublicViewAccessDeniedException(
          String.format("Failed to authorize public view access by authenticated session %s", sessionId));
    }

    return Optional.empty();
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizeProtectedViewAccess(String sessionId, String permissionId) {

    Sezzion existingSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authorize protected view access by non-existent session %s", sessionId)));

    if (existingSession.getExpiresAt().isBefore(Instant.now())) {
      throw new SessionExpiredException(
          String.format("Failed to authorize protected view access by expired session %s", sessionId));
    }

    if (existingSession.getUser() == null) {
      throw new ProtectedViewAccessDeniedException(
          String.format("Failed to authorize protected view access (permission %s) by anonymous session %s",
              permissionId, sessionId));
    }

    Collection<String> viewPermissionTokens = permissionService.hasViewPermission(existingSession.getUser().getRoleId(),
        permissionId).orElseThrow(
            () -> new ProtectedViewPermissionDeniedException(
                String.format("Failed to authorize protected view access (permission %s) by user %s", permissionId,
                    existingSession.getUser().getId())));

    return SezzionMapper.toDtoWithTokens(existingSession, viewPermissionTokens);
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizeApiAccess(String sessionId, String permissionId) {

    Sezzion existingSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authorize API access by non-existent session %s", sessionId)));

    if (existingSession.getExpiresAt().isBefore(Instant.now())) {
      throw new SessionExpiredException(
          String.format("Failed to authorize API access by expired session %s", sessionId));
    }

    if (existingSession.getUser() == null) {
      throw new ApiAccessDeniedException(
          String.format("Failed to authorize API access (permission %s) by anonymous session %s",
              permissionId, sessionId));
    }

    if (!permissionService.hasApiPermission(existingSession.getUser().getRoleId(), permissionId)) {
      throw new ApiPermissionDeniedException(
          String.format("Failed to authorize API access (permission %s) by user %s", permissionId,
              existingSession.getUser().getId()));
    }

    return SezzionMapper.toDto(existingSession);
  }

  // ---------------------------------------------------------------------------//

  public SezzionDTO authorizeStateChangingApiAccess(String sessionId, String permissionId, String syncToken) {

    Sezzion existingSession = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new SessionNotFoundException(
            String.format("Failed to authorize API access by non-existent session %s", sessionId)));

    if (existingSession.getExpiresAt().isBefore(Instant.now())) {
      throw new SessionExpiredException(
          String.format("Failed to authorize API access by expired session %s", sessionId));
    }

    if (existingSession.getUser() == null) {
      throw new ApiAccessDeniedException(
          String.format("Failed to authorize API access (permission %s) by anonymous session %s",
              permissionId, sessionId));
    }

    if (existingSession.getSyncToken() == null || !existingSession.getSyncToken().equals(syncToken)) {
      throw new InvalidSyncTokenException(String.format(
          "Failed to authorize API access (permission %s) by user %s due to missing or invalid synchronizer token",
          permissionId, existingSession.getUser().getId()));
    }

    if (!permissionService.hasApiPermission(existingSession.getUser().getRoleId(), permissionId)) {
      throw new ApiPermissionDeniedException(
          String.format("Failed to authorize API access (permission %s) by user %s", permissionId,
              existingSession.getUser().getId()));
    }

    return SezzionMapper.toDto(existingSession);
  }

}
