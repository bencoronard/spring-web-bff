package dev.hireben.demo.rest.web.bff.application.usecase;

import java.util.Collection;

import dev.hireben.demo.rest.web.bff.application.dto.UserDTO;
import dev.hireben.demo.rest.web.bff.application.service.PermissionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizationUseCase {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final PermissionService permissionService;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public boolean authorize(String permissionId, UserDTO user) {
    return permissionService.hasPermission(user.getRoleId(), permissionId);
  }

  // ---------------------------------------------------------------------------//

  public Collection<String> retrieveViewPermissions(String viewGroup, UserDTO user) {
    return permissionService.retrieveViewPermissions(user.getRoleId(), viewGroup);
  }

}
