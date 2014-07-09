package org.uberfire.security.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.uberfire.security.Role;
import org.uberfire.security.impl.RoleImpl;

/**
 * Custom role registry that allows to register roles for the application
 * that might be needed to be checked by using AutheticationManager specific
 * features such us for http based manager to rely on httpRequest.isUserInRole
 * as that might be mapped by using application server mechanism to abstract
 * repositories from application roles.
 * This registry should be used in case application defines roles and allows them
 * to be mapped with JEE server capabilities
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
}