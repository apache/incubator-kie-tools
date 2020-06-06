/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.rest.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.guvnor.rest.client.NewUser;
import org.guvnor.rest.client.Permission;
import org.guvnor.rest.client.PermissionException;
import org.guvnor.rest.client.PermissionResponse;
import org.guvnor.rest.client.PermissionType;
import org.guvnor.rest.client.ResourcePermission;
import org.guvnor.rest.client.UberfireRestResponse;
import org.guvnor.rest.client.UpdateSettingRequest;
import org.guvnor.rest.client.WorkbenchPermission;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.guvnor.structure.security.RepositoryAction.BUILD;
import static org.guvnor.structure.security.RepositoryAction.CREATE;
import static org.guvnor.structure.security.RepositoryAction.DELETE;
import static org.guvnor.structure.security.RepositoryAction.UPDATE;
import static org.uberfire.security.ResourceAction.READ;

/**
 * Utility class to perform various functions for the REST service involving user management operations
 */
@ApplicationScoped
public class UserManagementResourceHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementResourceHelper.class);

    private static final String EDIT_GLOBAL_PREFERENCES = "globalpreferences.edit";
    private static final String GUIDED_DECISION_TABLE_EDIT_COLUMNS = "guideddecisiontable.edit.columns";
    private static final String EDIT_PROFILE_PREFERENCES = "profilepreferences.edit";
    private static final String ACCESS_DATA_TRANSFER = "datatransfer.access";
    private static final String EDIT_SOURCES = "dataobject.edit";
    private static final String JAR_DOWNLOAD = "jar.download";
    private static final String PLANNER_AVAILABLE = "planner.available";

    @Inject
    private GroupManagerService groupManagerService;

    @Inject
    private UserManagerService userManagerService;

    @Inject
    private RoleManagerService roleManagerService;

    @Inject
    private AuthorizationService authorizationService;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private ResourceTypePermissionValidator permissionValidator;

    public UberfireRestResponse createGroup(final String groupName,
                                            final List<String> users) {
        UberfireRestResponse response = new UberfireRestResponse();
        if (groupName == null) {
            response.setStatus(Response.Status.BAD_REQUEST);
            response.setMessage("Group name cannot be empty");
        }
        Group group;
        try {
            group = groupManagerService.get(groupName);
            if (group != null) {
                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("Group with name " + groupName + " already exists");
            }
        } catch (GroupNotFoundException e) {
            if (!areUsersValid(users)) {
                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("Usernames are invalid, please check");
                return response;
            }
            group = groupManagerService.create(new GroupImpl(groupName));
            groupManagerService.assignUsers(groupName, users);
            if (group != null) {
                response.setStatus(Response.Status.OK);
                response.setMessage("Group " + group.getName() + " is created successfully.");
            } else {
                response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to create '" + groupName + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse createUser(NewUser newUser) {
        UberfireRestResponse response = new UberfireRestResponse();
        if (newUser.getName() == null) {
            response.setStatus(Response.Status.BAD_REQUEST);
            response.setMessage("User name cannot be empty");
            return response;
        }
        try {
            if (!areRolesValid(newUser.getRoles()) || !areGroupsValid(newUser.getGroups())) {
                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("User roles/groups are invalid. Please check ");
                return response;
            }

            User user = userManagerService.get(newUser.getName());
            if (user != null) {
                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("User with name " + newUser.getName() + " already exists");
            }
        } catch (UserNotFoundException e) {
            User userCreated = userManagerService.create(createUserObject(newUser));
            if (userCreated != null) {
                response.setStatus(Response.Status.OK);
                response.setMessage("User " + userCreated.getIdentifier() + " is created successfully.");
            } else {
                response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to create '" + newUser.getName() + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse changePassword(String user, String newPassword) {
        UberfireRestResponse response = new UberfireRestResponse();

        try {
            userManagerService.changePassword(user, newPassword);
            response.setStatus(Response.Status.OK);
            response.setMessage("Password for " + user + " has been updated successfully.");
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to update password for '" + user + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse removeGroup(final String groupName) {
        UberfireRestResponse response = new UberfireRestResponse();
        try {
            groupManagerService.delete(groupName);
            response.setStatus(Response.Status.OK);
            response.setMessage("Group " + groupName + " is deleted successfully.");
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to remove '" + groupName + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse removeUser(final String userName) {
        UberfireRestResponse response = new UberfireRestResponse();
        try {
            userManagerService.delete(userName);
            response.setStatus(Response.Status.OK);
            response.setMessage("User " + userName + " is deleted successfully.");
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to remove '" + userName + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse assignGroupsToUser(final String userName,
                                                   final List<String> groups) {
        UberfireRestResponse response = new UberfireRestResponse();
        try {
            if (!areGroupsValid(groups)) {
                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("Groups " + groups + " are not valid ");
                return response;
            }
            userManagerService.assignGroups(userName, groups);
            response.setStatus(Response.Status.OK);
            response.setMessage("Groups " + groups + " are assigned successfully to user " + userName);
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to assign groups to user  '" + userName + "': " + e.getMessage();
            logger.error(errMsg, e);

            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse assignRolesToUser(final String userName,
                                                  final List<String> roles) {
        UberfireRestResponse response = new UberfireRestResponse();
        try {
            if (!areRolesValid(roles)) {
                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("Roles " + roles + " are not valid ");
            }
            userManagerService.assignRoles(userName, roles);
            response.setStatus(Response.Status.OK);
            response.setMessage("Roles " + roles + " are assigned successfully to user " + userName);
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to assign roles to user  '" + userName + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public PermissionResponse getGroupPermissions(String groupName) {
        PermissionResponse permissionResponse = new PermissionResponse();
        Group group = groupManagerService.get(groupName);
        if (group != null) {
            AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
            PermissionCollection pc = authzPolicy.getPermissions(group);
            permissionResponse.setPriority(authzPolicy.getPriority(group));
            permissionResponse.setHomePage(authzPolicy.getHomePerspective(group));
            convertCollectionToPermissionResponse(permissionResponse, pc);
            permissionResponse.setWorkbench(getWorkbenchPermissions(pc));
        }
        return permissionResponse;
    }

    public PermissionResponse getRolePermissions(String roleName) {
        PermissionResponse permissionResponse = new PermissionResponse();
        Role role = roleManagerService.get(roleName);
        if (role != null) {
            AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
            PermissionCollection pc = authzPolicy.getPermissions(role);
            permissionResponse.setPriority(authzPolicy.getPriority(role));
            permissionResponse.setHomePage(authzPolicy.getHomePerspective(role));
            convertCollectionToPermissionResponse(permissionResponse, pc);
            permissionResponse.setWorkbench(getWorkbenchPermissions(pc));
        }
        return permissionResponse;
    }

    public PermissionResponse getUserPermissions(String userName) {
        PermissionResponse permissionResponse = new PermissionResponse();
        User user = userManagerService.get(userName);
        PermissionCollection pc = permissionManager.resolvePermissions(user, VotingStrategy.PRIORITY);
        convertCollectionToPermissionResponse(permissionResponse, pc);
        permissionResponse.setWorkbench(getWorkbenchPermissions(pc));
        return permissionResponse;
    }

    public UberfireRestResponse updateGroupPermissions(final String groupName,
                                                       final UpdateSettingRequest permissionsRequest) {
        UberfireRestResponse response = new UberfireRestResponse();
        try {
            Group group = groupManagerService.get(groupName);
            AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();

            if (permissionsRequest.getHomePage() != null && permissionValidator.isValidResourceType(ActivityResourceType.PERSPECTIVE, permissionsRequest.getHomePage())) {
                authzPolicy.setHomePerspective(group, permissionsRequest.getHomePage());
            }
            if (permissionsRequest.getPriority() != null) {
                authzPolicy.setPriority(group, permissionsRequest.getPriority());
            }

            PermissionCollection pc = authzPolicy.getPermissions(group);
            generatePermissionCollection(pc, permissionsRequest);
            authzPolicy.setPermissions(group, pc);

            authorizationService.savePolicy(authzPolicy);

            response.setStatus(Response.Status.OK);
            response.setMessage("Group " + groupName + " permissions are updated successfully.");
        } catch (GroupNotFoundException e) {
            response.setStatus(Response.Status.BAD_REQUEST);
            response.setMessage("Group with name " + groupName + "doesn't exists");
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to update permissions for  '" + groupName + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    public UberfireRestResponse updateRolePermissions(final String roleName,
                                                      final UpdateSettingRequest permissionsRequest) {
        UberfireRestResponse response = new UberfireRestResponse();
        try {
            Role role = roleManagerService.get(roleName);
            if (role != null) {
                AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
                if (permissionsRequest.getHomePage() != null && permissionValidator.isValidResourceType(ActivityResourceType.PERSPECTIVE, permissionsRequest.getHomePage())) {
                    authzPolicy.setHomePerspective(role, permissionsRequest.getHomePage());
                }
                if (permissionsRequest.getPriority() != null) {
                    authzPolicy.setPriority(role, permissionsRequest.getPriority());
                }

                PermissionCollection pc = authzPolicy.getPermissions(role);
                generatePermissionCollection(pc, permissionsRequest);
                authzPolicy.setPermissions(role, pc);

                authorizationService.savePolicy(authzPolicy);

                response.setStatus(Response.Status.OK);
                response.setMessage("Role " + roleName + " permissions are updated successfully.");
            } else {

                response.setStatus(Response.Status.BAD_REQUEST);
                response.setMessage("Role with name " + roleName + "doesn't exists");
            }
        } catch (Exception e) {
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to update permissions for  '" + roleName + "': " + e.getMessage();
            logger.error(errMsg, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
            response.setMessage(errMsg);
        }
        return response;
    }

    private void convertCollectionToPermissionResponse(PermissionResponse permissionResponse, PermissionCollection pc) {

        for (Map.Entry<ResourceType, List<PermissionAction>> entry : permissionValidator.getPermissionEntries()) {
            ResourcePermission permission = new ResourcePermission();
            ResourceType resourceType = entry.getKey();
            List<PermissionAction> resourceActions = entry.getValue();

            for (PermissionAction resourceAction : resourceActions) {
                PermissionType permissionType = getPermissionsType(pc, resourceType, resourceAction.getResourceAction());
                getPermissions(permission, resourceAction.getResourceAction(), permissionType);
            }

            if (resourceType.equals(ActivityResourceType.PERSPECTIVE)) {
                permissionResponse.setPages(permission);
            }
            if (resourceType.equals(OrganizationalUnit.RESOURCE_TYPE)) {
                permissionResponse.setSpaces(permission);
            }
            if (resourceType.equals(ActivityResourceType.EDITOR)) {
                permissionResponse.setEditor(permission);
            }
            if (resourceType.equals(Repository.RESOURCE_TYPE)) {
                permissionResponse.setProject(permission);
            }
        }
    }

    private WorkbenchPermission getWorkbenchPermissions(PermissionCollection pc) {
        WorkbenchPermission workbenchPermission = new WorkbenchPermission();
        workbenchPermission.setAccessDataTransfer(resolvePermission(pc, ACCESS_DATA_TRANSFER));
        workbenchPermission.setEditDataObject(resolvePermission(pc, EDIT_SOURCES));
        workbenchPermission.setEditGlobalPreferences(resolvePermission(pc, EDIT_GLOBAL_PREFERENCES));
        workbenchPermission.setEditProfilePreferences(resolvePermission(pc, EDIT_PROFILE_PREFERENCES));
        workbenchPermission.setJarDownload(resolvePermission(pc, JAR_DOWNLOAD));
        workbenchPermission.setPlannerAvailable(resolvePermission(pc, PLANNER_AVAILABLE));
        workbenchPermission.setEditGuidedDecisionTableColumns(resolvePermission(pc, GUIDED_DECISION_TABLE_EDIT_COLUMNS));
        return workbenchPermission;
    }

    private boolean resolvePermission(PermissionCollection pc, String permission) {
        org.uberfire.security.authz.Permission resolvedPermission = pc.get(permission);
        if (resolvedPermission != null) {
            AuthorizationResult result = resolvedPermission.getResult();
            return result.equals(AuthorizationResult.ACCESS_GRANTED);
        }
        return false;
    }

    private void getPermissions(ResourcePermission permission, ResourceAction resourceAction, PermissionType permissionType) {
        if (resourceAction.equals(READ)) {
            permission.setRead(permissionType);
        }
        if (resourceAction.equals(CREATE)) {
            permission.setCreate(permissionType);
        }
        if (resourceAction.equals(BUILD)) {
            permission.setBuild(permissionType);
        }
        if (resourceAction.equals(UPDATE)) {
            permission.setUpdate(permissionType);
        }
        if (resourceAction.equals(DELETE)) {
            permission.setDelete(permissionType);
        }
    }

    private PermissionType getPermissionsType(PermissionCollection pc, ResourceType resourceType, ResourceAction resourceAction) {

        PermissionType permissionType = new PermissionType();
        List<String> exceptions = new ArrayList<>();

        String permissionName = resourceType.getName() + "." + resourceAction.getName();
        org.uberfire.security.authz.Permission parentPermission = pc.get(permissionName);

        if (parentPermission != null) {
            AuthorizationResult result = parentPermission.getResult();
            permissionType.setAccess(result.equals(AuthorizationResult.ACCESS_GRANTED));

            for (org.uberfire.security.authz.Permission permission : pc.collection()) {
                if (parentPermission.impliesName(permission) && !parentPermission.impliesResult(permission)) {
                    String resourceId = permissionManager.resolveResourceId(permission);
                    exceptions.add(resourceId);
                }
            }
            permissionType.setExceptions(exceptions);
        }
        return permissionType;
    }

    private PermissionCollection generatePermissionCollection(PermissionCollection pc, UpdateSettingRequest permissionRequest) {
        if (permissionRequest.getPages() != null) {
            addToCollection(pc, ActivityResourceType.PERSPECTIVE, permissionRequest.getPages());
        }
        if (permissionRequest.getSpaces() != null) {
            addToCollection(pc, OrganizationalUnit.RESOURCE_TYPE, permissionRequest.getSpaces());
        }
        if (permissionRequest.getProject() != null) {
            addToCollection(pc, Repository.RESOURCE_TYPE, permissionRequest.getProject());
        }
        if (permissionRequest.getEditor() != null) {
            addToCollection(pc, ActivityResourceType.EDITOR, permissionRequest.getEditor());
        }
        if (permissionRequest.getWorkbench() != null) {
            addWorkBenchPermissions(pc, permissionRequest.getWorkbench());
        }
        return pc;
    }

    private void addWorkBenchPermissions(PermissionCollection pc, WorkbenchPermission permission) {

        if (permission.getAccessDataTransfer() != null) {
            pc.add(permissionManager.createPermission(ACCESS_DATA_TRANSFER, permission.getAccessDataTransfer()));
        }
        if (permission.getEditDataObject() != null) {
            pc.add(permissionManager.createPermission(EDIT_SOURCES, permission.getEditDataObject()));
        }
        if (permission.getEditGlobalPreferences() != null) {
            pc.add(permissionManager.createPermission(EDIT_GLOBAL_PREFERENCES, permission.getEditGlobalPreferences()));
        }
        if (permission.getEditProfilePreferences() != null) {
            pc.add(permissionManager.createPermission(EDIT_PROFILE_PREFERENCES, permission.getEditProfilePreferences()));
        }
        if (permission.getJarDownload() != null) {
            pc.add(permissionManager.createPermission(JAR_DOWNLOAD, permission.getJarDownload()));
        }
        if (permission.getPlannerAvailable() != null) {
            pc.add(permissionManager.createPermission(PLANNER_AVAILABLE, permission.getPlannerAvailable()));
        }
        if (permission.getEditGuidedDecisionTableColumns() != null) {
            pc.add(permissionManager.createPermission(GUIDED_DECISION_TABLE_EDIT_COLUMNS, permission.getEditGuidedDecisionTableColumns()));
        }
    }

    private void addToCollection(PermissionCollection pc, ResourceType resourceType, Permission permission) {

        if (permission.isRead() != null && permissionValidator.isPermissionAllowed(resourceType, READ) && permissionValidator.satisfyDependancies(pc, resourceType, READ)) {
            pc.add(permissionManager.createPermission(resourceType, READ, permission.isRead()));
        }
        if (permission.isCreate() != null && permissionValidator.isPermissionAllowed(resourceType, CREATE) && permissionValidator.satisfyDependancies(pc, resourceType, CREATE)) {
            pc.add(permissionManager.createPermission(resourceType, CREATE, permission.isCreate()));
        }
        if (permission.isUpdate() != null && permissionValidator.isPermissionAllowed(resourceType, UPDATE) && permissionValidator.satisfyDependancies(pc, resourceType, UPDATE)) {
            pc.add(permissionManager.createPermission(resourceType, UPDATE, permission.isUpdate()));
        }
        if (permission.isDelete() != null && permissionValidator.isPermissionAllowed(resourceType, DELETE) && permissionValidator.satisfyDependancies(pc, resourceType, DELETE)) {
            pc.add(permissionManager.createPermission(resourceType, DELETE, permission.isDelete()));
        }
        if (permission.isBuild() != null && permissionValidator.isPermissionAllowed(resourceType, BUILD) && permissionValidator.satisfyDependancies(pc, resourceType, BUILD)) {
            pc.add(permissionManager.createPermission(resourceType, BUILD, permission.isBuild()));
        }
        if (permission.getExceptions() != null) {
            addExceptions(pc, resourceType, permission.getExceptions());
        }
    }

    private void addExceptions(PermissionCollection pc, ResourceType resourceType, List<PermissionException> exceptions) {
        for (PermissionException exception : exceptions) {
            Permission permission = exception.getPermissions();
            String resourceTypeName = resourceType.getName();
            if (permissionValidator.isValidResourceType(resourceType, exception.getResourceName())) {
                if (permission.isRead() != null && permissionValidator.isPermissionAllowed(resourceType, READ)) {
                    final String permissionName = resourceTypeName + "." + READ.getName() + "." + exception.getResourceName();
                    pc.add(permissionManager.createPermission(permissionName, permission.isRead()));
                } else if (permission.isCreate() != null && permissionValidator.isPermissionAllowed(resourceType, CREATE)) {
                    final String permissionName = resourceTypeName + "." + CREATE.getName() + "." + exception.getResourceName();
                    pc.add(permissionManager.createPermission(permissionName, permission.isCreate()));
                } else if (permission.isUpdate() != null && permissionValidator.isPermissionAllowed(resourceType, UPDATE)) {
                    final String permissionName = resourceTypeName + "." + UPDATE.getName() + "." + exception.getResourceName();
                    pc.add(permissionManager.createPermission(permissionName, permission.isUpdate()));
                } else if (permission.isDelete() != null && permissionValidator.isPermissionAllowed(resourceType, DELETE)) {
                    final String permissionName = resourceTypeName + "." + DELETE.getName() + "." + exception.getResourceName();
                    pc.add(permissionManager.createPermission(permissionName, permission.isDelete()));
                }
            }
        }
    }

    private User createUserObject(NewUser newUser) {
        final Collection<Role> userRoles = new HashSet<>();
        final Collection<Group> userGroups = new HashSet<>();
        if (newUser.getRoles() != null) {
            for (final String roleName : newUser.getRoles()) {
                Role role = new RoleImpl(roleName);
                userRoles.add(role);
            }
        }
        if (newUser.getGroups() != null) {
            for (final String groupName : newUser.getGroups()) {
                Group group = new GroupImpl(groupName);
                userGroups.add(group);
            }
        }
        return new UserImpl(newUser.getName(), userRoles, userGroups);
    }

    private boolean areUsersValid(List<String> users) {
        if (users != null) {
            try {
                for (final String username : users) {
                    User user = userManagerService.get(username);
                    if (user == null) {
                        return false;
                    }
                }
            } catch (UserNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    private boolean areGroupsValid(List<String> groups) {
        if (groups != null) {
            for (final String groupName : groups) {
                try {
                    Group group = groupManagerService.get(groupName);
                    if (group == null) {
                        return false;
                    }
                } catch (GroupNotFoundException e) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean areRolesValid(List<String> roles) {
        if (roles != null) {
            for (final String roleName : roles) {
                Role role = roleManagerService.get(roleName);
                if (role == null) {
                    return false;
                }
            }
        }
        return true;
    }
}

