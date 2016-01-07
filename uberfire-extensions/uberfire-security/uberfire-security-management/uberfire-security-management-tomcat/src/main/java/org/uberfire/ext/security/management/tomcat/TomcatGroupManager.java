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

import org.apache.catalina.users.MemoryUserDatabase;
import org.jboss.errai.security.shared.api.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.impl.GroupManagerSettingsImpl;
import org.uberfire.ext.security.management.search.GroupsIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import java.util.*;

/**
 * <p>Groups manager service provider implementation for Apache tomcat, when using default realm based on properties files.</p>
 *
 * @since 0.8.0
 */
public class TomcatGroupManager extends BaseTomcatManager implements GroupManager, ContextualManager {

    private static final Logger LOG = LoggerFactory.getLogger(TomcatGroupManager.class);

    IdentifierRuntimeSearchEngine<Group> groupsSearchEngine;
    
    public TomcatGroupManager() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    public TomcatGroupManager(final Map<String, String> gitPrefs) {
        this( new ConfigProperties( gitPrefs ) );
    }

    public TomcatGroupManager(final ConfigProperties gitPrefs) {
        loadConfig( gitPrefs );
    }
    
    @Override
    public void initialize(UserSystemManager userSystemManager) throws Exception {
        groupsSearchEngine = new GroupsIdentifierRuntimeSearchEngine();
    }

    @Override
    public void destroy() throws Exception {
        
    }

    @Override
    public SearchResponse<Group> search(SearchRequest request) throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            Iterator<org.apache.catalina.Role> groups = userDatabase.getRoles();
            Collection<String> groupIdentifiers = new ArrayList<String>();
            if (groups != null) {
                while (groups.hasNext()) {
                    org.apache.catalina.Role group = groups.next();
                    String groupname = group.getRolename();
                    groupIdentifiers.add(groupname);
                }
            }
            return groupsSearchEngine.searchByIdentifiers(groupIdentifiers, request);
        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public Group get(String identifier) throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            org.apache.catalina.Role group = getRole(userDatabase, identifier);
            return createGroup(group);
        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public Group create(Group entity) throws SecurityManagementException {
        if (entity == null) throw new NullPointerException();
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            String name  = entity.getName();
            userDatabase.createRole(name, name);
            saveDatabase(userDatabase);
            return entity;
        } finally {
            closeDatabase(userDatabase);
        }
        
    }

    @Override
    public Group update(Group entity) throws SecurityManagementException {
        throw new UnsupportedServiceCapabilityException(Capability.CAN_UPDATE_GROUP);
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        if (identifiers == null) throw new NullPointerException();
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            for (String identifier : identifiers) {
                org.apache.catalina.Role group = getRole(userDatabase, identifier);
                userDatabase.removeRole(group);
            }
            saveDatabase(userDatabase);
        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public GroupManagerSettings getSettings() {
        final Map<Capability, CapabilityStatus> capabilityStatusMap = new HashMap<Capability, CapabilityStatus>(8);
        for (final Capability capability : SecurityManagementUtils.GROUPS_CAPABILITIES) {
            capabilityStatusMap.put(capability, getCapabilityStatus(capability));
        }
        return new GroupManagerSettingsImpl(capabilityStatusMap, true);
    }
    
    protected CapabilityStatus getCapabilityStatus(Capability capability) {
        if (capability != null) {
            switch (capability) {
                case CAN_SEARCH_GROUPS:
                case CAN_ADD_GROUP:
                case CAN_READ_GROUP:
                case CAN_DELETE_GROUP:
                    return CapabilityStatus.ENABLED;
            }
        }
        return CapabilityStatus.UNSUPPORTED;
    }

    @Override
    public void assignUsers(String name, Collection<String> users) throws SecurityManagementException {
        if (name == null) throw new NullPointerException();
        if (users != null) {
            MemoryUserDatabase userDatabase = getDatabase();
            org.apache.catalina.Role role = getRole(userDatabase, name);
            try {
                for (String username : users) {
                    org.apache.catalina.User user = getUser(userDatabase, username);
                    user.addRole(role);
                }
                saveDatabase(userDatabase);
            } finally {
                closeDatabase(userDatabase);
            }

        }
    }

}
