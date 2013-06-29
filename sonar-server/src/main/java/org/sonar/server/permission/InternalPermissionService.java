/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerComponent;
import org.sonar.api.security.DefaultGroups;
import org.sonar.core.user.*;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.user.UserSession;

import java.util.List;
import java.util.Map;

/**
 * Used by ruby code <pre>Internal.permissions</pre>
 */
public class InternalPermissionService implements ServerComponent {

  private static final Logger LOG = LoggerFactory.getLogger(InternalPermissionService.class);

  private static final String ADD = "add";
  private static final String REMOVE = "remove";

  private final RoleDao roleDao;
  private final UserDao userDao;

  public InternalPermissionService(RoleDao roleDao, UserDao userDao) {
    this.roleDao = roleDao;
    this.userDao = userDao;
  }

  public void addPermission(Map<String, Object> params) {
    changePermission(ADD, params);
  }

  public void removePermission(Map<String, Object> params) {
    changePermission(REMOVE, params);
  }

  private void changePermission(String permissionChange, Map<String, Object> params) {
    UserSession.get().checkPermission(Permissions.SYSTEM_ADMIN);
    PermissionChangeQuery permissionChangeQuery = PermissionChangeQuery.buildFromParams(params);
    if(permissionChangeQuery.isValid()) {
      if(Permissions.SYSTEM_ADMIN.equals(permissionChangeQuery.getRole()) && REMOVE.equals(permissionChange)) {
        checkThatAtLeastOneAdminRemains(permissionChangeQuery);
      }
      applyPermissionChange(permissionChange, permissionChangeQuery);
    } else {
      String errorMsg = String.format("Request '%s permission %s' is invalid", permissionChange, permissionChangeQuery.getRole());
      LOG.error(errorMsg);
      throw new IllegalArgumentException(errorMsg);
    }
  }

  private void applyPermissionChange(String operation, PermissionChangeQuery permissionChangeQuery) {
    if(permissionChangeQuery.targetsUser()) {
      applyUserPermissionChange(operation, permissionChangeQuery);
    } else {
      applyGroupPermissionChange(operation, permissionChangeQuery);
    }
  }

  private void applyGroupPermissionChange(String operation, PermissionChangeQuery permissionChangeQuery) {
    List<String> existingPermissions = roleDao.selectGroupPermissions(permissionChangeQuery.getGroup());
    if(shouldSkipPermissionChange(operation, existingPermissions, permissionChangeQuery.getRole())) {
      LOG.info("Skipping permission change '{} {}' for group {} as it matches the current permission scheme",
        new String[]{operation, permissionChangeQuery.getRole(), permissionChangeQuery.getGroup()});
    } else {
      Long targetedGroup = getTargetedGroup(permissionChangeQuery.getGroup());
      GroupRoleDto groupRole = new GroupRoleDto().setRole(permissionChangeQuery.getRole()).setGroupId(targetedGroup);
      if(ADD.equals(operation)) {
        roleDao.insertGroupRole(groupRole);
      } else {
        roleDao.deleteGroupRole(groupRole);
      }
    }
  }

  private void applyUserPermissionChange(String operation, PermissionChangeQuery permissionChangeQuery) {
    List<String> existingPermissions = roleDao.selectUserPermissions(permissionChangeQuery.getUser());
    if(shouldSkipPermissionChange(operation, existingPermissions, permissionChangeQuery.getRole())) {
      LOG.info("Skipping permission change '{} {}' for user {} as it matches the current permission scheme",
        new String[]{operation, permissionChangeQuery.getRole(), permissionChangeQuery.getUser()});
    } else {
      Long targetedUser = getTargetedUser(permissionChangeQuery.getUser());
      UserRoleDto userRole = new UserRoleDto().setRole(permissionChangeQuery.getRole()).setUserId(targetedUser);
      if(ADD.equals(operation)) {
        roleDao.insertUserRole(userRole);
      } else {
        roleDao.deleteUserRole(userRole);
      }
    }
  }

  private Long getTargetedUser(String userLogin) {
    return userDao.selectActiveUserByLogin(userLogin).getId();
  }

  private Long getTargetedGroup(String group) {
    if(DefaultGroups.isAnyone(group)) {
      return null;
    }
    return userDao.selectGroupByName(group).getId();
  }

  private boolean shouldSkipPermissionChange(String operation, List<String> existingPermissions, String role) {
    return (ADD.equals(operation) && existingPermissions.contains(role)) ||
      (REMOVE.equals(operation) && !existingPermissions.contains(role));
  }

  private void checkThatAtLeastOneAdminRemains(PermissionChangeQuery permissionChangeQuery) {
    int remainingSystemAdmins = roleDao.countSystemAdministrators(permissionChangeQuery.getGroup());
    if(remainingSystemAdmins == 0) {
      String errorMsg = String.format("Cannot remove permission %s to %s - At least one system administrator should remain active",
        permissionChangeQuery.getRole(), permissionChangeQuery.getUser() == null ? permissionChangeQuery.getGroup() : permissionChangeQuery.getUser());
      LOG.error(errorMsg);
      throw new BadRequestException(errorMsg);
    }
  }
}