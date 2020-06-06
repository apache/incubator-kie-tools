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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.jboss.errai.security.shared.api.Role;
import org.uberfire.backend.server.security.RoleRegistry;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.RoleManager;
import org.uberfire.ext.security.management.api.RoleManagerSettings;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.search.RolesRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.RuntimeSearchEngine;

/**
 * <p>The default role manager implementation for UF based applications.</p>
 * <p>Roles are not coming from any external system, are just the ones registered in the application</p>
 * <p>CRUD operations for roles are not allowed, only search is allowed.</p>
 * @since 0.8.0
 */
@Dependent
@Named("uberfireRoleManager")
public class UberfireRoleManager implements RoleManager,
                                            ContextualManager {

    UserSystemManager userSystemManager;
    RuntimeSearchEngine<Role> rolesSearchEngine;

    @Override
    public void initialize(final UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
        rolesSearchEngine = new RolesRuntimeSearchEngine();
    }

    @Override
    public SearchResponse<Role> search(SearchRequest request) throws SecurityManagementException {
        return rolesSearchEngine.search(RoleRegistry.get().getRegisteredRoles(),
                                        request);
    }

    @Override
    public Role get(String identifier) throws SecurityManagementException {
        return RoleRegistry.get().getRegisteredRole(identifier);
    }

    @Override
    public List<Role> getAll() throws SecurityManagementException {
        return new ArrayList<>(RoleRegistry.get().getRegisteredRoles());
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
}
