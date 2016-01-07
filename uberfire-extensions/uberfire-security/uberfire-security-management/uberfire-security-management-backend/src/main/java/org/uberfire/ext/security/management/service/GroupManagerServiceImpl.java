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

package org.uberfire.ext.security.management.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.BackendUserSystemManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.GroupManagerSettings;
import org.uberfire.ext.security.management.api.exception.NoImplementationAvailableException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

/**
 * <p>The UberFire service implementation for GroupsManager API.</p>
 */
@Service
@ApplicationScoped
public class GroupManagerServiceImpl implements GroupManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupManagerServiceImpl.class);
    
    @Inject
    private BackendUserSystemManager userSystemManager;
    
    private GroupManager service;
    
    @PostConstruct
    public void init() {
        service = userSystemManager.groups();
    }
    
    private GroupManager getService() throws SecurityManagementException {
        if (service == null) throw new NoImplementationAvailableException();
        return service;
    }

    @Override
    public SearchResponse<Group> search(SearchRequest request) throws SecurityManagementException {
        final GroupManager serviceImpl = getService();
        if (request.getPage() == 0) throw new IllegalArgumentException("First page must be 1.");
        
        // Constraint registered UF roles as not allowed for searching. 
        final Set<String> registeredRoleNames = SecurityManagementUtils.getRegisteredRoleNames();
        if ( request.getConstrainedIdentifiers() == null ) {
            request.setConstrainedIdentifiers(registeredRoleNames);
        } else {
            request.getConstrainedIdentifiers().addAll(registeredRoleNames);
        }
        
        // Delegate the search to the specific provider.
        return serviceImpl.search(request);
    }

    @Override
    public Group get(String identifier) throws SecurityManagementException {
        final GroupManager serviceImpl = getService();
        return serviceImpl.get(identifier);
    }

    @Override
    public Group create(Group group) throws SecurityManagementException {
        final String name = group.getName();
        if (isConstrained(name)) {
            throw new IllegalArgumentException("Group with name '" + name + "' cannot be created, " +
                    "as it is a constrained value (it is a role or the admin group");
        }
        final GroupManager serviceImpl = getService();
        return serviceImpl.create(group);
    }

    @Override
    public Group update(Group group) throws SecurityManagementException {
        final String name = group.getName();
        if (isConstrained(name)) {
            throw new IllegalArgumentException("Group with name '" + name + "' cannot be updated, " +
                    "as it is a constrained value (it is a role or the admin group");
        }
        final GroupManager serviceImpl = getService();
        return serviceImpl.update(group);
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        for (final String name : identifiers)  {
            if (isConstrained(name)) {
                throw new IllegalArgumentException("Group with name '" + name + "' cannot be deleted, " +
                        "as it is a constrained value (it is a role or the admin group");
            }
        }
        final GroupManager serviceImpl = getService();
        serviceImpl.delete(identifiers);
    }

    @Override
    public GroupManagerSettings getSettings() {
        final GroupManager serviceImpl = getService();
        final GroupManagerSettings settings = serviceImpl.getSettings();
        if ( null != settings ) {
            settings.setConstrainedGroups(SecurityManagementUtils.getRegisteredRoleNames());
        }
        return settings;
    }

    @Override
    public void assignUsers(String name, Collection<String> users) throws SecurityManagementException {
        final GroupManager serviceImpl = getService();
        serviceImpl.assignUsers(name, users);
    }

    protected boolean isConstrained(final String name) {
        return SecurityManagementUtils.getRegisteredRoleNames().contains(name);
    }

}
