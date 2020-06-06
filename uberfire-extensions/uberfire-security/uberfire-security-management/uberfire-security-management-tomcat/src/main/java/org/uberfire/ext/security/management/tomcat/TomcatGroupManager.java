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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.catalina.users.MemoryUserDatabase;
import org.jboss.errai.security.shared.api.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.GroupManagerSettings;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.impl.GroupManagerSettingsImpl;
import org.uberfire.ext.security.management.search.GroupsIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

/**
 * <p>Groups manager service provider implementation for Apache tomcat, when using default realm based on properties files.</p>
 * @since 0.8.0
 */
public class TomcatGroupManager extends BaseTomcatManager implements GroupManager,
                                                                     ContextualManager {

    private static final Logger LOG = LoggerFactory.getLogger(TomcatGroupManager.class);

    IdentifierRuntimeSearchEngine<Group> groupsSearchEngine;

    public TomcatGroupManager() {
        this(new ConfigProperties(System.getProperties()));
    }

    public TomcatGroupManager(final Map<String, String> gitPrefs) {
        this(new ConfigProperties(gitPrefs));
    }

    public TomcatGroupManager(final ConfigProperties gitPrefs) {
        loadConfig(gitPrefs);
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
        List<Group> groups  = getAll();
        return groupsSearchEngine.search(groups, request);
    }

    @Override
    public Group get(String identifier) throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            org.apache.catalina.Role group = getRole(userDatabase,
                                                     identifier);
            return createGroup(group);
        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public List<Group> getAll() throws SecurityManagementException {
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            Iterator<org.apache.catalina.Role> groupIterator = userDatabase.getRoles();
            List<Group> groups = new ArrayList<>();
            if (groupIterator != null) {
                while (groupIterator.hasNext()) {
                    org.apache.catalina.Role group = groupIterator.next();
                    Group groupname = SecurityManagementUtils.createGroup(group.getRolename());
                    groups.add(groupname);
                }
            }
            return groups;
        } finally {
            closeDatabase(userDatabase);
        }
    }

    @Override
    public Group create(Group entity) throws SecurityManagementException {
        if (entity == null) {
            throw new NullPointerException();
        }
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            String name = entity.getName();
            userDatabase.createRole(name,
                                    name);
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
        if (identifiers == null) {
            throw new NullPointerException();
        }
        MemoryUserDatabase userDatabase = getDatabase();
        try {
            for (String identifier : identifiers) {
                org.apache.catalina.Role group = getRole(userDatabase,
                                                         identifier);
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
            capabilityStatusMap.put(capability,
                                    getCapabilityStatus(capability));
        }
        return new GroupManagerSettingsImpl(capabilityStatusMap,
                                            true);
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
    public void assignUsers(String name,
                            Collection<String> users) throws SecurityManagementException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (users != null) {
            MemoryUserDatabase userDatabase = getDatabase();
            org.apache.catalina.Role role = getRole(userDatabase,
                                                    name);
            try {
                for (String username : users) {
                    org.apache.catalina.User user = getUser(userDatabase,
                                                            username);
                    user.addRole(role);
                }
                saveDatabase(userDatabase);
            } finally {
                closeDatabase(userDatabase);
            }
        }
    }
}
