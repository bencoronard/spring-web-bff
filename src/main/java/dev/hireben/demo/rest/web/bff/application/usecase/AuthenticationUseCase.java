package dev.hireben.demo.rest.web.bff.application.usecase;

import dev.hireben.demo.rest.web.bff.application.dto.AuthenticationDTO;
import dev.hireben.demo.rest.web.bff.application.dto.UserDTO;
import dev.hireben.demo.rest.web.bff.application.exception.UserAuthenticationException;
import dev.hireben.demo.rest.web.bff.application.mapper.UserInfoMapper;
import dev.hireben.demo.rest.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.rest.web.bff.domain.model.UserInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final AuthenticationService authenticationService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public UserDTO authenticate(AuthenticationDTO dto) {

    UserInfo user = authenticationService.authenticate(dto.getUsername(), dto.getPassword())
        .orElseThrow(() -> new UserAuthenticationException("Failed to authenticate user"));

    return UserInfoMapper.toDto(user);
  }

}
