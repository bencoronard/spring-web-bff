package dev.hireben.demo.rest.web.bff.application.usecase;

import java.time.Instant;

import dev.hireben.demo.rest.web.bff.application.dto.CreateUserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.dto.UserDTO;
import dev.hireben.demo.rest.web.bff.application.dto.UserSessionDTO;
import dev.hireben.demo.rest.web.bff.application.mapper.UserSessionMapper;
import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.model.UserInfo;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUserSessionUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final UserSessionRepository repository;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserSessionDTO createSession(CreateUserSessionDTO dto, UserDTO user) {

    UserInfo userInfo = UserInfo.builder()
        .id(user.getId())
        .badgeId(user.getBadgeId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .tenant(user.getTenant())
        .position(user.getPosition())
        .roleId(user.getRoleId())
        .build();

    UserSession session = UserSession.builder()
        .user(userInfo)
        .createdAt(Instant.now())
        .expiresAt(dto.getExpiresAt())
        .build();

    return UserSessionMapper.toDto(repository.save(session));
  }

}
