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

import org.uberfire.ext.security.management.UberfireRoleManager;
import org.uberfire.ext.security.management.api.RoleManager;
import org.uberfire.ext.security.management.api.UserManagementService;

import javax.inject.Inject;

/**
 * <p>The base user management service that uses the <code>org.uberfire.ext.security.management.UberfireRoleManager</code> 
 * as the role manager service used in the platform.</p>
 * 
 * @since 0.8.0
 */
public abstract class AbstractUserManagementService implements UserManagementService {

    UberfireRoleManager roleManager;

    @Inject
    public AbstractUserManagementService(UberfireRoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Override
    public RoleManager roles() {
        return roleManager;
    }
}
