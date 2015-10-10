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

package org.uberfire.ext.security.management.tomcat;

import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.RoleManager;
import org.uberfire.ext.security.management.api.UserManagementService;
import org.uberfire.ext.security.management.api.UserManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>The Tomcat management service beans.</p>
 * 
 * @since 0.8.0
 */
@ApplicationScoped
@Named(value = "TomcatUserManagementService")
public class TomcatUserManagementService implements UserManagementService {

    @Inject
    TomcatUserManager userManager;
    
    @Inject
    TomcatGroupManager groupManager;

    @Override
    public UserManager users() {
        return userManager;
    }

    @Override
    public GroupManager groups() {
        return groupManager;
    }

    @Override
    public RoleManager roles() {
        throw new UnsupportedOperationException("Roles are not supported.");
    }
}
