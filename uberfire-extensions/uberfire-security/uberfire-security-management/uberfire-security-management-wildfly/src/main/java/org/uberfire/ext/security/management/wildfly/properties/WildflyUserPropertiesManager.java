/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.wildfly.properties;

import org.jboss.as.domain.management.security.UserPropertiesFileLoader;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.msc.service.StartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.UsersIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Users manager service provider implementation for JBoss Wildfly, when using default realm based on properties files.</p>
 * 
 * @since 0.8.0
 */
public class WildflyUserPropertiesManager extends BaseWildflyPropertiesManager implements UserManager, ContextualManager {

    public static final String DEFAULT_USERS_FILE = "./standalone/configuration/application-users.properties";
    public static final String DEFAULT_PASSWORD = "";
    private static final Logger LOG = LoggerFactory.getLogger(WildflyUserPropertiesManager.class);

    protected final IdentifierRuntimeSearchEngine<User> usersSearchEngine = new UsersIdentifierRuntimeSearchEngine();
    protected UserSystemManager userSystemManager;
    protected String usersFilePath;
    UserPropertiesFileLoader usersFileLoader;


    public WildflyUserPropertiesManager() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    public WildflyUserPropertiesManager(final Map<String, String> gitPrefs) {
        this( new ConfigProperties( gitPrefs ) );
    }

    public WildflyUserPropertiesManager(final ConfigProperties gitPrefs) {
        loadConfig( gitPrefs );
    }

    protected void loadConfig( final ConfigProperties config ) {
        LOG.debug("Configuring JBoss Wildfly provider from properties.");
        super.loadConfig(config);
        final ConfigProperties.ConfigProperty usersFilePathProperty = config.get("org.uberfire.ext.security.management.wildfly.properties.users-file-path", DEFAULT_USERS_FILE);
        if (!isConfigPropertySet(usersFilePathProperty)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.wildfly.properties.users-file-path' is mandatory and not set.");
        this.usersFilePath = usersFilePathProperty.getValue();
        LOG.debug("Configuration of JBoss Wildfly provider provider finished.");
    }

    @Override
    public void initialize(UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
        getUsersFileLoader();
    }

    @Override
    public void destroy() throws Exception {
        getUsersFileLoader().stop(null);
    }

    @Override
    public SearchResponse<User> search(SearchRequest request) throws SecurityManagementException {
        List<String> users = getUserNames();
        return usersSearchEngine.searchByIdentifiers(users, request);
    }

    @Override
    public User get(String identifier) throws SecurityManagementException {
        List<String> userNames = getUserNames();
        if (userNames != null && userNames.contains(identifier)) {
            if (getGroupsPropertiesManager() != null) {
                final Set<Group> userGroups = getGroupsPropertiesManager().getGroupsForUser(identifier);
                return SecurityManagementUtils.createUser(identifier, userGroups);
            } else {
                return SecurityManagementUtils.createUser(identifier);
            }
        }
        throw new UserNotFoundException(identifier);
    }

    public String getUsersFilePath() {
        return usersFilePath;
    }
    
    @Override
    public User create(User entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();
        updateUserProperty(entity.getIdentifier(), "Error creating user." + entity.getIdentifier());
        return entity;
    }

    @Override
    public User update(User entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();
        updateUserProperty(entity.getIdentifier(), "Error updating user " + entity.getIdentifier());
        return entity;
    }

    @Override
    public void delete(String... usernames) throws SecurityManagementException {
        if (usernames == null) throw new NullPointerException();
        for (String username : usernames) {
            final User user = get(username);
            if (user == null) throw new UserNotFoundException(username);
            try {
                usersFileLoader.getProperties().remove(username);
                usersFileLoader.persistProperties();
            } catch (IOException e) {
                LOG.error("Error removing user " + username, e);
                throw new SecurityManagementException(e);
            }
        }
    }
    
    @Override
    public void assignGroups(String username, Collection<String> groups) throws SecurityManagementException {
        if (getGroupsPropertiesManager() != null) {
            getGroupsPropertiesManager().setGroupsForUser(username, groups);
        }
    }

    @Override
    public void assignRoles(String username, Collection<String> roles) throws SecurityManagementException {
        throw new UnsupportedServiceCapabilityException(Capability.CAN_ASSIGN_ROLES);
    }

    @Override
    public void changePassword(String username, String newPassword) throws SecurityManagementException {
        if (username == null) throw new NullPointerException();
        if (newPassword != null) {
            updateUserProperty(username, generateHashPassword(username, realm, newPassword), "Error changing user's password.");
        }
    }

    @Override
    public CapabilityStatus getCapabilityStatus(Capability capability) {
        if (capability != null) {
            switch (capability) {
                case CAN_SEARCH_USERS:
                case CAN_ADD_USER:
                case CAN_UPDATE_USER:
                case CAN_DELETE_USER:
                case CAN_READ_USER:
                case CAN_ASSIGN_GROUPS:
                case CAN_CHANGE_PASSWORD:
                    return CapabilityStatus.ENABLED;
            }
        }
        return CapabilityStatus.UNSUPPORTED;
    }

    @Override
    public Collection<UserAttribute> getAttributes() {
        // Properties based realm does not have attribute support.
        return null;
    }

    protected  UserPropertiesFileLoader buildFileLoader(String usersFilePath) throws Exception {
        File usersFile = new File(usersFilePath);
        if (!usersFile.exists()) throw new RuntimeException("Properties file for users not found at '" + usersFilePath + "'.");

        this.usersFileLoader = new UserPropertiesFileLoader(usersFile.getAbsolutePath());
        try {
            this.usersFileLoader.start(null);
        } catch (StartException e) {
            throw new IOException(e);
        }

        return this.usersFileLoader;

    }

    protected  List<String> getUserNames() {
        try {
            return usersFileLoader.getEnabledUserNames();
        } catch (Exception e) {
            LOG.error("Error obtaining JBoss Wildfly users from properties file.", e);
            throw new SecurityManagementException(e);
        }
    }

    protected  void updateUserProperty(final String username, final String errorMessage) {
        updateUserProperty(username, null, errorMessage);
    }

    protected  void updateUserProperty(final String username, final String password, final String errorMessage) {
        if (username != null) {
            try {
                String p = password != null ? password : usersFileLoader.getProperties().getProperty(username);
                p = p != null ? p : DEFAULT_PASSWORD;
                usersFileLoader.getProperties().put(username, p);
                usersFileLoader.persistProperties();
            } catch (IOException e) {
                LOG.error(errorMessage, e);
                throw new SecurityManagementException(e);
            }
        }
    }

    // Does not need to synchronize as the manager implements ContextualManager.
    protected UserPropertiesFileLoader getUsersFileLoader() throws Exception {
        if (usersFileLoader == null) {
            this.usersFileLoader = buildFileLoader(getUsersFilePath());
        }
        return usersFileLoader;
    }

    protected synchronized WildflyGroupPropertiesManager getGroupsPropertiesManager() {
        try {
            return (WildflyGroupPropertiesManager) userSystemManager.groups();
        } catch (ClassCastException e) {
            return null;
        }
    }
    
}
