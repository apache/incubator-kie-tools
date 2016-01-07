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

package org.uberfire.ext.security.management;

import org.jboss.errai.security.shared.api.Role;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.search.RolesRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.RuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>The default role manager implementation for UF based applications.</p>
 * <p>Roles are not coming from any external system, are just the ones registered in the application</p>
 * <p>CRUD operations for roles are not allowed, only search is allowed.</p>
 * 
 * @since 0.8.0
 */
@Dependent
@Named("uberfireRoleManager")
public class UberfireRoleManager implements RoleManager, ContextualManager {

    UserSystemManager userSystemManager;
    RuntimeSearchEngine<Role> rolesSearchEngine;
    
    @Override
    public void initialize(final UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
        rolesSearchEngine = new RolesRuntimeSearchEngine();
    }
    
    @Override
    public SearchResponse<Role> search(SearchRequest request) throws SecurityManagementException {
        return rolesSearchEngine.search(getRegisteredRoles(), request);
    }

    @Override
    public Role get(String identifier) throws SecurityManagementException {
        throw new UnsupportedOperationException("Get operation is not available when using the UberfireRoleManager provider.");
    }

    @Override
    public Role create(Role entity) throws SecurityManagementException {
        throw new UnsupportedOperationException("Create operation is not available when using the UberfireRoleManager provider.");
    }

    @Override
    public Role update(Role entity) throws SecurityManagementException {
        throw new UnsupportedOperationException("Update operation is not available when using the UberfireRoleManager provider.");
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        throw new UnsupportedOperationException("Delete operation is not available when using the UberfireRoleManager provider.");
    }

    @Override
    public RoleManagerSettings getSettings() {
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }

    // Check that roles registered in the framework are created as realm groups as well, if not they cannot be used, as it couldn't be assigned.
    protected Set<Role> getRegisteredRoles() {
        final Set<Role> result = new LinkedHashSet<Role>();
        final Set<Role> registeredRoles = SecurityManagementUtils.getRegisteredRoles();
        if ( null != registeredRoles && !registeredRoles.isEmpty() ) {
            for (final Role registeredRole : registeredRoles) {
                if ( existGroup(registeredRole.getName()) ) {
                    result.add(registeredRole);
                }
            }
        }
        
        return result;
    }
    
    protected boolean existGroup(final String name) {
        try {
            // If the groupManager does not found the role name, it will  throw an exception, 
            // so the role will be not added into the resulting list.
            GroupManager groupManager = userSystemManager.groups();
            groupManager.get(name);
            return true;
        } catch (GroupNotFoundException e) {
            // Registered role not found as realm group.
            return false;
        }
    }

}
