/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;

/**
 * TODO: update me
 */
public class RolesRegistry {

    private Set<Role> roles = new HashSet<Role>();

    private static RolesRegistry instance = null;

    private RolesRegistry() {
    }

    /**
     * Returns singleton instance of the registry to be able to register roles
     * @return
     */
    public static RolesRegistry get() {
        if ( instance == null ) {
            instance = new RolesRegistry();
        }

        return instance;
    }

    /**
     * Registers given <code>role</code> into the registry
     * @param role
     */
    public void registerRole( String role ) {
        this.roles.add( new RoleImpl( role ) );
    }

    /**
     * Returns unmodifiable copy of all reqistered roles
     * @return
     */
    public Set<Role> getRegisteredRoles() {
        return Collections.unmodifiableSet( this.roles );
    }

    /**
     * Clears the registry.
     */
    public void clear() {
        this.roles.clear();
    }

}
