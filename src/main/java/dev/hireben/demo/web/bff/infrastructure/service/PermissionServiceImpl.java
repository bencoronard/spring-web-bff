package dev.hireben.demo.web.bff.infrastructure.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.hireben.demo.web.bff.application.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public Optional<Collection<String>> hasViewPermission(String roleId, String permissionId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'hasViewPermission'");
  }

  // ---------------------------------------------------------------------------//

  @Override
  public boolean hasApiPermission(String roleId, String permissionId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'hasApiPermission'");
  }

}
