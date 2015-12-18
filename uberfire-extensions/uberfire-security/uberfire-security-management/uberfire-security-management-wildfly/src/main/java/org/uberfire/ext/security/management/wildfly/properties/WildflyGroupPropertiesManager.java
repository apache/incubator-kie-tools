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

import org.apache.commons.lang3.StringUtils;
import org.jboss.as.domain.management.security.PropertiesFileLoader;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.impl.GroupManagerSettingsImpl;
import org.uberfire.ext.security.management.search.GroupsIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>Groups manager service provider implementation for JBoss Wildfly, when using default realm based on properties files.</p>
 * 
 * @since 0.8.0
 */
public class WildflyGroupPropertiesManager extends BaseWildflyPropertiesManager implements GroupManager, ContextualManager {

    public static final String DEFAULT_GROUPS_FILE = "./standalone/configuration/application-roles.properties";
    public static final String DEFAULT_GROUPS = ",";
    private static final Logger LOG = LoggerFactory.getLogger(WildflyGroupPropertiesManager.class);
    private static final String GROUP_SEPARATOR = ",";

    protected UserSystemManager userSystemManager;
    protected  final IdentifierRuntimeSearchEngine<Group> groupsSearchEngine = new GroupsIdentifierRuntimeSearchEngine();
    protected  String groupsFilePath;
    protected  PropertiesFileLoader groupsPropertiesFileLoader;


    public WildflyGroupPropertiesManager() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    public WildflyGroupPropertiesManager(final Map<String, String> gitPrefs) {
        this( new ConfigProperties( gitPrefs ) );
    }

    public WildflyGroupPropertiesManager(final ConfigProperties gitPrefs) {
        loadConfig( gitPrefs );
    }

    protected void loadConfig( final ConfigProperties config ) {
        LOG.debug("Configuring JBoss Wildfly provider from properties.");
        super.loadConfig(config);
        final ConfigProperties.ConfigProperty groupsFilePathProperty = config.get("org.uberfire.ext.security.management.wildfly.properties.groups-file-path", DEFAULT_GROUPS_FILE);
        if (!isConfigPropertySet(groupsFilePathProperty)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.wildfly.properties.groups-file-path' is mandatory and not set.");
        this.groupsFilePath = groupsFilePathProperty.getValue();
        LOG.debug("Configuration of JBoss Wildfly provider provider finished.");
    }

    @Override
    public void initialize(UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
        this.groupsPropertiesFileLoader = getFileLoader(getGroupsFilePath());
    }

    @Override
    public void destroy() throws Exception {
        this.groupsPropertiesFileLoader.stop(null);
    }
    
    @Override
    public SearchResponse<Group> search(SearchRequest request) throws SecurityManagementException {
        Set<String> result = getAllGroups();
        return groupsSearchEngine.searchByIdentifiers(result, request);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Group get(String identifier) throws SecurityManagementException {
        if (identifier == null) throw new NullPointerException();
        Set<String> result = getAllGroups();
        if (result != null && result.contains(identifier)) return createGroup(identifier);
        throw new GroupNotFoundException(identifier);
    }

    public Set<Group> getGroupsForUser(String username) {
        if (groupsPropertiesFileLoader != null && username != null) {
            try {
                final String groupsStr = groupsPropertiesFileLoader.getProperties().getProperty(username);
                final Set<String> groups = parseGroupIdentifiers(groupsStr);
                if (groups != null) {
                    final Set<String> allGroups = getAllGroups();
                    if (allGroups != null) {
                        final Set<Group> result = new HashSet<Group>(groups.size());
                        for (final String name : groups) {
                            if (!allGroups.contains(name)) {
                                String error = "Error getting groups for user. User's group '" + name + "' does not exist.";
                                LOG.error(error);
                                throw new SecurityManagementException(error);
                            }
                            result.add(createGroup(name));
                        }
                        return result;
                        
                    }
                }
            } catch (IOException e) {
                LOG.error("Error getting groups for user " + username, e);
                throw new SecurityManagementException(e);
            }
        }
        return null;
    }

    public void setGroupsForUser(String username, Collection<String> groups) {
        if (username == null) throw new NullPointerException();
        final String errorMsg = "Error updating groups for user " + username;
        final String g = groups != null ? StringUtils.join(groups, ',') : DEFAULT_GROUPS;
        if (groups != null && !existGroups(groups)) {
            LOG.error(errorMsg);
            throw new SecurityManagementException(errorMsg);
        }
        updateGroupProperty(username, g, errorMsg);
    }
    
    public String getGroupsFilePath() {
        return groupsFilePath;
    }

    /**
     * Wildfly / EAP realms based on properties do not allow groups with empty users. So the groups are created using the method #assignUsers.
     * @param entity The entity to create.
     * @return A runtime instance for a group.
     * @throws SecurityManagementException
     */
    @Override
    public Group create(Group entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();
        return new GroupImpl(entity.getName());
    }

    @Override
    public Group update(Group entity) throws SecurityManagementException {
        throw new UnsupportedServiceCapabilityException(Capability.CAN_UPDATE_GROUP);
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        if (identifiers == null) throw new NullPointerException();
        try {
            Set<Map.Entry<Object, Object>> propertiesSet = groupsPropertiesFileLoader.getProperties().entrySet();
            if (!propertiesSet.isEmpty()) {
                for (Map.Entry<Object, Object> entry : propertiesSet) {
                    final String username = entry.getKey().toString();
                    final String groupsStr = entry.getValue().toString();
                    if (groupsStr != null && groupsStr.trim().length() > 0) {
                        final String newGroupsStr = deleteGroupsFromSerliazedValue(groupsStr, identifiers);
                        final String errorMsg = "Error deleting groups for user " + username;
                        updateGroupProperty(username, newGroupsStr, errorMsg);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error removing the folowing group names: " + identifiers, e);
            throw new SecurityManagementException(e);
        }
    }
    
    private String deleteGroupsFromSerliazedValue(String groupsStr, String... identifiers) {
        if (groupsStr != null && groupsStr.trim().length() > 0) {
            String[] gs = groupsStr.split(",");
            Set<String> groupSet = new HashSet<String>(gs.length);
            Collections.addAll(groupSet, gs);
            for (String name : identifiers) {
                groupSet.remove(name);
            }
            return StringUtils.join(groupSet, ',');
        }
        return null;
    }

    @Override
    public void assignUsers(String name, Collection<String> users) throws SecurityManagementException {
        if (name == null) throw new NullPointerException();
        if (users != null) {
            if (users.isEmpty()) throw new RuntimeException("The realm based on properties file does not allow groups with no users assigned.");
            for (String username : users) {
                try {
                    final String groupsStr = groupsPropertiesFileLoader.getProperties().getProperty(username);
                    Set<String> groupSet = null;
                    if (groupsStr != null && groupsStr.trim().length() > 0) {
                        String[] gs = groupsStr.split(",");
                        groupSet = new HashSet<String>(gs.length);
                        Collections.addAll(groupSet, gs);
                    } else {
                        groupSet = new HashSet<String>(1);
                    }
                    groupSet.add(name);
                    final String errorMsg = "Error updating groups for user " + username;
                    final String newGroupsStr = StringUtils.join(groupSet, ',');
                    updateGroupProperty(username, newGroupsStr, errorMsg);
                } catch (IOException e) {
                    LOG.error("Error setting groups for user " + username, e);
                    throw new SecurityManagementException(e);
                }
            }
        }
    }

    @Override
    public GroupManagerSettings getSettings() {
        final Map<Capability, CapabilityStatus> capabilityStatusMap = new HashMap<Capability, CapabilityStatus>(8);
        for (final Capability capability : SecurityManagementUtils.GROUPS_CAPABILITIES) {
            capabilityStatusMap.put(capability, getCapabilityStatus(capability));
        }
        return new GroupManagerSettingsImpl(capabilityStatusMap, false);
    }
    
    protected CapabilityStatus getCapabilityStatus(Capability capability) {
        if (capability != null) {
            switch (capability) {
                case CAN_ADD_GROUP:
                case CAN_DELETE_GROUP:
                case CAN_SEARCH_GROUPS:
                case CAN_READ_GROUP:
                    return CapabilityStatus.ENABLED;
            }
        }
        return CapabilityStatus.UNSUPPORTED;
    }

    protected  Group createGroup(String name) {
        return SecurityManagementUtils.createGroup(name);
    }
    
    @SuppressWarnings(value = "unchecked")
    protected Set<String> getAllGroups() {
        try {
            Collection<Object> values = groupsPropertiesFileLoader.getProperties().values();
            final HashSet<String> result = new HashSet<String>();
            for (Object value : values) {
                Set<String> s = parseGroupIdentifiers(value.toString());
                if (s != null) {
                    result.addAll(s);
                }
            }
            return result;
        } catch (IOException e) {
            LOG.error("Error getting all groups.", e);
            throw new SecurityManagementException(e);
        }
    }

    protected  void updateGroupProperty(final String name, final String groups, final String errorMessage) {
        if (name != null) {
            try {
                String g = groups != null ? groups : groupsPropertiesFileLoader.getProperties().getProperty(name);
                g = g != null ? g : DEFAULT_GROUPS;
                groupsPropertiesFileLoader.getProperties().put(name, g);
                groupsPropertiesFileLoader.persistProperties();
            } catch (IOException e) {
                LOG.error(errorMessage, e);
                throw new SecurityManagementException(e);
            }
        }
    }

    protected  static Set<String> parseGroupIdentifiers(String groupsStr) {
        if (groupsStr != null && groupsStr.trim().length() > 0) {
            String[] groupsArray = groupsStr.split(GROUP_SEPARATOR);
            Set<String> result = new HashSet<String>(groupsArray.length);
            Collections.addAll(result, groupsArray);
            return result;
        }
        return null;
    }

    protected  boolean existGroups(final Collection<String> groups) {
        if (groups != null) {
            Set<String> allGroups = getAllGroups();
            if (allGroups != null && !allGroups.isEmpty()) {
                for (String name : groups) {
                    if (!allGroups.contains(name)) return false;
                }
                return true;
            }
        }
        return false;
    }

    protected  PropertiesFileLoader getFileLoader(String filePath) {
        File propertiesFile = new File(filePath);
        if (!propertiesFile.exists()) throw new RuntimeException("Cannot load roles/groups properties file from '" + filePath + "'.");

        PropertiesFileLoader propertiesLoad = null;
        try {
            propertiesLoad = new PropertiesFileLoader(propertiesFile.getCanonicalPath());
            propertiesLoad.start(null);
        } catch (Exception e) {
            LOG.error("Error getting properties file.", e);
            throw new SecurityManagementException(e);
        }

        return propertiesLoad;
    }

    protected synchronized WildflyUserPropertiesManager getUsersPropertiesManager() {
        try {
            return (WildflyUserPropertiesManager) userSystemManager.users();
        } catch (ClassCastException e) {
            return null;
        }
    }
    
}
