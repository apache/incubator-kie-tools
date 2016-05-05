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

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;

import java.util.Collection;

/**
 * <p>The Users Manager.</p>
 * <p>The user manager provide additional methods to implement from the entity manager ones.</p>
 * 
 * @since 0.8.0
 */
public interface UserManager extends AbstractEntityManager<User, UserManagerSettings> {
    
    /**
     * <p>Assign a collection of groups to a user.</p>.
     * <p>All the groups given will be assigned to the user, the current existing assigned ones will be removed.</p>
     * <p>It's important to note that the inherited <code>create</code> and <code>update</code> methods should not assign groups, this operations must be done in this method, 
     * as it's easier for mapping with the capabilities approach.</p>
     * @param username The user identifier.
     * @param groups The collection of groups identifiers to assign.
     * @throws SecurityManagementException
     */
    void assignGroups(String username, Collection<String> groups) throws SecurityManagementException;

    /**
     * <p>Assign a collection of roles to a user.</p>.
     * <p>All the roles given will be assigned to the user, the current existing assigned ones will be removed.</p>
     * <p>It's important to note that the inherited <code>create</code> and <code>update</code> methods should not assign roles, this operations must be done in this method, 
     * as it's easier for mapping with the capabilities approach.</p>
     * @param username The user identifier.
     * @param roles The collection of roles identifiers to assign.
     * @throws SecurityManagementException
     */
    void assignRoles(String username, Collection<String> roles) throws SecurityManagementException;

    /**
     * <p>Changes the user's password.</p>
     * @param username The user identifier.
     * @param newPassword The new password.
     * @throws SecurityManagementException
     */
    void changePassword(String username, String newPassword) throws SecurityManagementException;

    /**
     * <p>Description of a user attribute that is supported by the specific manager implementation.</p>
     */
    interface UserAttribute {

        /**
         * The attribute name.
         * @return The attribute name.
         */
        String getName();

        /**
         * Specifies if the attribute is required for creating a user or if it cannot be removed.
         * @return Is mandatory.
         */
        boolean isMandatory();

        /**
         * Specifies if the attribute can be modified.
         * @return Is editable.
         */
        boolean isEditable();

        /**
         * The default value for an attribute.
         * Used when creating a new user and setting mandatory attributes.
         * @return The default value for the attribute, if any.
         */
        String getDefaultValue();
    }
}
