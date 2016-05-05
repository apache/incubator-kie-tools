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

/**
 * <p>The services provided by a concrete user management service provider implementation.</p>
 * <p>Each concrete service provider has to implement this interface to provider the different managers.
 * Note that the service is found given a CDI bean name, eg: <code>Wildfly</code>, 
 * so this interface defines the users, groups and roles manager beans to use when using the <code>Wildfly</code> or whatever the implementation is.</p>
 * 
 * @since 0.8.0
 */
public interface UserManagementService {

    /**
     * <p>The Users Manager service.</p>
     * @return The concrete users manager service implementation used by this provider.
     */
    UserManager users();

    /**
     * <p>The Groups Manager service.</p>
     * @return The concrete groups manager service implementation used by this provider.
     */
    GroupManager groups();

    /**
     * <p>The Roles Manager service.</p>
     * @return The concrete roles manager service implementation used by this provider.
     */
    RoleManager roles();
    
}
