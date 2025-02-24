package dev.hireben.demo.rest.web.bff.application.service;

import java.util.Collection;

public interface PermissionService {

  Collection<String> retrieveViewPermissions(String roleId, String viewGroupId);

  boolean hasPermission(String roleId, String permissionId);

}
