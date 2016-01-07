/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.impl.SearchResponseImpl;
import org.uberfire.ext.security.management.impl.UserManagerSettingsImpl;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * <p>UsersManager Service Provider Implementation for KeyCloak.</p>
 * 
 * @since 0.8.0
 */
public class KeyCloakUserManager extends BaseKeyCloakManager implements UserManager, ContextualManager {

    private static final Logger LOG = LoggerFactory.getLogger(KeyCloakUserManager.class);
    private static final String CREDENTIAL_TYPE_PASSWORD = "password";

    UserSystemManager userSystemManager;
    
    public KeyCloakUserManager() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    public KeyCloakUserManager(final Map<String, String> gitPrefs ) {
        this( new ConfigProperties( gitPrefs ) );
    }
    
    public KeyCloakUserManager(final ConfigProperties gitPrefs) {
        loadConfig( gitPrefs );
    }

    @Override
    public void initialize(final UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
    }

    @Override
    public SearchResponse<User> search(SearchRequest request) throws SecurityManagementException {
        final SearchRequest req = getSearchRequest(request);
        // First page must be 1.
        if (req.getPage() <= 0) throw new RuntimeException("First page must be 1.");
        final int page = req.getPage() - 1;
        final int pageSize = req.getPageSize();
        RealmResource realmResource = getRealmResource();
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> userRepresentations = usersResource.search(req.getSearchPattern(), page * pageSize, pageSize + 1);
        final List<User> users = new ArrayList<User>();
        boolean hasNext = false;
        if (userRepresentations != null && !userRepresentations.isEmpty()) {
            int x = 0;
            for (UserRepresentation userRepresentation : userRepresentations) {
                if (x == req.getPageSize()) {
                    hasNext = true;
                } else {
                    final User user = createUser(userRepresentation);
                    users.add(user);
                    x++;
                }
            }
        } 
        
        return new SearchResponseImpl<User>(users, page + 1, pageSize, -1, hasNext);
    }

    @Override
    public User get(String username) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();
        RealmResource realmResource = getRealmResource();
        UsersResource usersResource = realmResource.users();
        UserResource userResource = getUserResource(usersResource, username);
        RoleMappingResource roleMappingResource = userResource.roles();
        Set<Group> _groups = null;
        Set<Role> _roles = null;
        if (roleMappingResource != null) {
            Set[] gr = getUserGroupsAndRoles(roleMappingResource);
            if ( null != gr ) {
                _groups = gr[0];
                _roles = gr[1];
            }
        }
        User user = createUser(userResource.toRepresentation(), _groups, _roles);
        return user;
    }
    
    @Override
    public User create(User entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();
        RealmResource realmResource = getRealmResource();
        UsersResource usersResource = realmResource.users();
        UserRepresentation userRepresentation = new UserRepresentation();
        fillUserRepresentationAttributes(entity, userRepresentation);
        Response response = usersResource.create(userRepresentation);
        handleResponse(response);
        return entity;
    }
    
    @Override
    public User update(User entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();
        UsersResource usersResource = getRealmResource().users();
        UserResource userResource = getUserResource(usersResource, entity.getIdentifier());
        if (userResource == null) throw new UserNotFoundException(entity.getIdentifier());
        UserRepresentation userRepresentation = new UserRepresentation();
        fillUserRepresentationAttributes(entity, userRepresentation);
        userResource.update(userRepresentation);
        return entity;
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        if (identifiers == null) throw new NullPointerException();
        RealmResource realmResource = getRealmResource();
        UsersResource usersResource = realmResource.users();
        for (String identifier : identifiers) {
            UserResource userResource = getUserResource(usersResource, identifier);
            if (userResource == null) throw new UserNotFoundException(identifier);
            userResource.remove();
        }
    }

    @Override
    public UserManagerSettings getSettings() {
        final Map<Capability, CapabilityStatus> capabilityStatusMap = new HashMap<Capability, CapabilityStatus>(8);
        for (final Capability capability : SecurityManagementUtils.USERS_CAPABILITIES) {
            capabilityStatusMap.put(capability, getCapabilityStatus(capability));
        }
        return new UserManagerSettingsImpl(capabilityStatusMap, USER_ATTRIBUTES);
    }

    @Override
    public void assignGroups(String username, Collection<String> groups) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();
        Set<String> userRoles = SecurityManagementUtils.rolesToString(SecurityManagementUtils.getRoles(userSystemManager, username));
        userRoles.addAll(groups);
        assignGroupsOrRoles(username, userRoles);
    }

    @Override
    public void assignRoles(String username, Collection<String> roles) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();
        Set<String> userGroups = SecurityManagementUtils.groupsToString(SecurityManagementUtils.getGroups(userSystemManager, username));
        userGroups.addAll(roles);
        assignGroupsOrRoles(username, userGroups);
    }

    private void assignGroupsOrRoles(String username, Collection<String> idsToAssign) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();
        RealmResource realmResource = getRealmResource();
        UsersResource usersResource = realmResource.users();
        UserResource userResource = getUserResource(usersResource, username);
        if (userResource == null) throw new UserNotFoundException(username);
        org.keycloak.admin.client.resource.RolesResource rolesResource = realmResource.roles();
        List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listEffective();
        userResource.roles().realmLevel().remove(roleRepresentations);

        if (idsToAssign != null && !idsToAssign.isEmpty()) {

            // Add the given assignments.
            List<RoleRepresentation> rolesToAdd = new ArrayList<RoleRepresentation>();
            for (String name : idsToAssign) {
                RoleResource roleResource = rolesResource.get(name);
                if (roleResource != null) {
                    rolesToAdd.add(getRoleRepresentation(name, roleResource));
                }
            }

            userResource.roles().realmLevel().add(rolesToAdd);

        }

    }
    
    @Override
    public void changePassword(String username, String newPassword) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();
        RealmResource realmResource = getRealmResource();
        UsersResource usersResource = realmResource.users();
        UserResource userResource = getUserResource(usersResource, username);
        if (userResource == null) throw new UserNotFoundException(username);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CREDENTIAL_TYPE_PASSWORD);
        credentialRepresentation.setValue(newPassword);
        userResource.resetPassword(credentialRepresentation);
    }

    protected CapabilityStatus getCapabilityStatus(final Capability capability) {
        if (capability != null) {
            switch (capability) {
                case CAN_SEARCH_USERS:
                case CAN_ADD_USER:
                case CAN_UPDATE_USER:
                case CAN_DELETE_USER:
                case CAN_READ_USER:
                case CAN_MANAGE_ATTRIBUTES:
                case CAN_ASSIGN_GROUPS:
                /** As it is using the UberfireRoleManager. **/
                case CAN_ASSIGN_ROLES:
                case CAN_CHANGE_PASSWORD:
                    return CapabilityStatus.ENABLED;
            }
        }
        return CapabilityStatus.UNSUPPORTED;
    }

    @Override
    public void destroy() throws Exception {

    }
    
}
