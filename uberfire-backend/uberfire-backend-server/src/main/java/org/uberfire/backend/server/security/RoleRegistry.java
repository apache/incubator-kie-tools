/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;

/**
 * It holds the collection of Role instances that the platform security
 * services recognize as the application available roles.
 */
public class RoleRegistry {

    private static RoleRegistry instance = null;
    private Set<Role> roles = new HashSet<Role>();

    private RoleRegistry() {
    }

    /**
     * Returns singleton instance of the registry to be able to register roles
     */
    public static RoleRegistry get() {
        if (instance == null) {
            instance = new RoleRegistry();
        }
        return instance;
    }

    /**
     * Registers given <code>role</code> into the registry
     */
    public void registerRole(String role) {
        this.roles.add(new RoleImpl(role));
    }

    /**
     * Gets a a role instance by its name or null if not found.
     */
    public Role getRegisteredRole(String name) {
        for (Role role : roles) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    /**
     * /**
     * Returns unmodifiable copy of all reqistered roles
     */
    public Set<Role> getRegisteredRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    /**
     * Clears the registry.
     */
    public void clear() {
        this.roles.clear();
    }
}
