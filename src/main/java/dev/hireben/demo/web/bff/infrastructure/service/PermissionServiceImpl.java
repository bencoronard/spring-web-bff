package dev.hireben.demo.web.bff.infrastructure.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

import dev.hireben.demo.web.bff.application.service.PermissionService;

@Service
@SuppressWarnings("unused")
public class PermissionServiceImpl implements PermissionService {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final Map<String, String> MOCK_VIEW_PERMISSIONS_MAP = Map.of(
      "/consent", "CONSENT_OVERVIEW",
      "/notification", "NOTIFICATION_OVERVIEW",
      "/insurance", "INSURANCE_OVERVIEW",
      "/auction", "AUCTION_OVERVIEW");
  private final Map<String, String> MOCK_API_PERMISSIONS_MAP = Map.of(
      "GET/resources", "RESOURCE_LIST",
      "GET/resources/*", "RESOURCE_READ",
      "POST/resources", "RESOURCE_CREATE",
      "PATCH/resources/*", "RESOURCE_UPDATE",
      "PUT/resources/*", "RESOURCE_REPLACE",
      "DELETE/resources/*", "RESOURCE_DELETE");
  private final Map<String, Object> MOCK_ROLE_PERMISSIONS_MAP = null;

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
