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

package org.uberfire.ext.security.management.api;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.validation.EntityValidator;

/**
 * <p>Main users, groups and roles management API.</p>
 * <p>It provides a centralized entry point for using the different entities management API.</p>
 * <p>By default, two implementation are provided. A given one for the backend side and another one for the client side.</p>
 * @see <a>org.uberfire.ext.security.management.BackendUserSystemManager</a>
 * @see <a>org.uberfire.ext.security.management.client.ClientUserSystemManager</a>
 * 
 * @since 0.8.0
 */
public interface UserSystemManager extends UserManagementService {

    /* Constrained role name used by the platform. */
    String ADMIN = "admin";
    
    /**
     * <p>The user validator.</p>
     * @return The user validator instance.
     */
    EntityValidator<User> usersValidator();

    /**
     * <p>The group validator.</p>
     * @return The group validator instance.
     */
    EntityValidator<Group> groupsValidator();

    /**
     * <p>The role validator.</p>
     * @return The role validator instance.
     */
    EntityValidator<Role> rolesValidator();

    /**
     * Check if the user system management service are active and successfully initialized. 
     */
    boolean isActive();
}
