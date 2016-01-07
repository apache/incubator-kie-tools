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

package org.uberfire.ext.security.management.tomcat;

import org.apache.catalina.Role;
import org.apache.catalina.users.MemoryUserDatabase;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.impl.UserManagerSettingsImpl;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.UsersIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import java.util.*;

/**
 * <p>Users manager service provider implementation for Apache tomcat, when using default realm based on properties files.</p>
 *
 * @since 0.8.0
 */
public class TomcatUserManager extends BaseTomcatManager implements UserManager, ContextualManager {

    private static final Logger LOG = LoggerFactory.getLogger(TomcatUserManager.class);

    UserSystemManager userSystemManager;
    IdentifierRuntimeSearchEngine<User> usersSearchEngine;
    
    public TomcatUserManager() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    public TomcatUserManager(final Map<String, String> gitPrefs) {
        this( new ConfigProperties( gitPrefs ) );
    }

    public TomcatUserManager(final ConfigProperties gitPrefs) {
        loadConfig( gitPrefs );
    }
    
    @Override
    public void initialize(final UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
        usersSearchEngine = new UsersIdentifierRuntimeSearchEngine();
    }

    @Override
    public void destroy() throws Exception {
        
    }

    @Override
    public SearchResponse<User> search(SearchRequest request) throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            Iterator<org.apache.catalina.User> users = userDatabase.getUsers();
            Collection<String> userIdentifiers = new ArrayList<String>();
            if (users != null) {
                while (users.hasNext()) {
                    org.apache.catalina.User user = users.next();
                    String username = user.getUsername();
                    userIdentifiers.add(username);
                }
            }
            return usersSearchEngine.searchByIdentifiers(userIdentifiers, request);
        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public User get(String identifier) throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            org.apache.catalina.User user = getUser(userDatabase, identifier);
            Iterator<Role> groups = user.getRoles();
            
            User  u = createUser(user, groups);
            u.setProperty(ATTRIBUTE_USER_FULLNAME, user.getFullName() != null ? user.getFullName() : "");
            return u;

        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public User create(User entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();

        MemoryUserDatabase userDatabase = getDatabase();
        try {
            String username = entity.getIdentifier();
            String fullName = entity.getProperty(ATTRIBUTE_USER_FULLNAME);
            userDatabase.createUser(username, "", fullName != null ? fullName : "");
            saveDatabase(userDatabase);
            return entity;

        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public User update(User entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();

        MemoryUserDatabase userDatabase = getDatabase();
        try {
            org.apache.catalina.User user = getUser(userDatabase, entity.getIdentifier());
            String fName = entity.getProperty(ATTRIBUTE_USER_FULLNAME);
            user.setFullName(fName != null ? fName : "");
            saveDatabase(userDatabase);
            return entity;

        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        if (identifiers == null) throw new NullPointerException();
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            for (String identifier : identifiers) {
                org.apache.catalina.User user = getUser(userDatabase, identifier);
                userDatabase.removeUser(user);
            }
            saveDatabase(userDatabase);
        } finally {
            closeDatabase(userDatabase);
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
        Set<String> userRoles = SecurityManagementUtils.rolesToString(SecurityManagementUtils.getRoles(userSystemManager, username));
        userRoles.addAll(groups);
        doAssignGroups(username, userRoles);        
    }

    @Override
    public void assignRoles(String username, Collection<String> roles) throws SecurityManagementException {
        Set<String> userGroups = SecurityManagementUtils.groupsToString(SecurityManagementUtils.getGroups(userSystemManager, username));
        userGroups.addAll(roles);
        doAssignGroups(username, userGroups);
    }

    private void doAssignGroups(String username, Collection<String> ids) throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            org.apache.catalina.User user = getUser(userDatabase, username);
            user.removeRoles();
            if (!ids.isEmpty()) {
                for (String roleName : ids) {
                    org.apache.catalina.Role role = getRole(userDatabase, roleName);
                    user.addRole(role);
                }
            }
            saveDatabase(userDatabase);

        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public void changePassword(String username, String newPassword) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();

        MemoryUserDatabase userDatabase = getDatabase();
        try {
            org.apache.catalina.User user = getUser(userDatabase, username);
            user.setPassword(newPassword);
            saveDatabase(userDatabase);

        } finally {
            closeDatabase(userDatabase);
        }
    }

    protected CapabilityStatus getCapabilityStatus(Capability capability) {
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
}
