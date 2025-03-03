package dev.hireben.demo.web.bff.application.service;

import java.util.Collection;
import java.util.Optional;

public interface PermissionService {

  Optional<Collection<String>> hasViewPermission(String roleId, String permissionId);

  boolean hasApiPermission(String roleId, String permissionId);

}
