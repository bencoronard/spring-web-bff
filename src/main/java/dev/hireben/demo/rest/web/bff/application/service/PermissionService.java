package dev.hireben.demo.rest.web.bff.application.service;

import java.util.Collection;

public interface PermissionService {

  Collection<String> hasViewPermission(String roleId, String permissionId);

  boolean hasApiPermission(String roleId, String permissionId);

}
