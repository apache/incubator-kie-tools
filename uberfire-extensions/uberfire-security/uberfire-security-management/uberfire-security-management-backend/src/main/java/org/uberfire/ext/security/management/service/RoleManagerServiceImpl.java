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

package org.uberfire.ext.security.management.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.BackendUserSystemManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.RoleManager;
import org.uberfire.ext.security.management.api.exception.NoImplementationAvailableException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The UberFire service implementation for RolesManager API.</p>
 */
@Service
@ApplicationScoped
public class RoleManagerServiceImpl implements RoleManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleManagerServiceImpl.class);
    
    @Inject
    private BackendUserSystemManager userSystemManager;
    
    private RoleManager service;
    
    @PostConstruct
    public void init() {
        service = userSystemManager.roles();
    }
    
    private RoleManager getService() throws SecurityManagementException {
        if (service == null) throw new NoImplementationAvailableException();
        return service;
    }

    @Override
    public SearchResponse<Role> search(SearchRequest request) throws SecurityManagementException {
        final RoleManager serviceImpl = getService();
        if (request.getPage() == 0) throw new IllegalArgumentException("First page must be 1.");
        return serviceImpl.search(request);
    }

    @Override
    public Role get(String identifier) throws SecurityManagementException {
        final RoleManager serviceImpl = getService();
        return serviceImpl.get(identifier);
    }

    @Override
    public Role create(Role entity) throws SecurityManagementException {
        final RoleManager serviceImpl = getService();
        return serviceImpl.create(entity);
    }

    @Override
    public Role update(Role entity) throws SecurityManagementException {
        final RoleManager serviceImpl = getService();
        return serviceImpl.update(entity);
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        final RoleManager serviceImpl = getService();
        serviceImpl.delete(identifiers);
    }

    @Override
    public CapabilityStatus getCapabilityStatus(Capability capability) {
        final RoleManager serviceImpl = getService();
        return serviceImpl.getCapabilityStatus(capability);
    }

    @Override
    public Map<Capability, CapabilityStatus> getCapabilities() {
        final RoleManager serviceImpl = getService();
        final Map<Capability, CapabilityStatus> capabilityStatusMap = new HashMap<Capability, CapabilityStatus>(8);
        for (final Capability capability : SecurityManagementUtils.ROLES_CAPABILITIES) {
            capabilityStatusMap.put(capability, serviceImpl.getCapabilityStatus(capability));
        }
        return capabilityStatusMap;
    }
}
