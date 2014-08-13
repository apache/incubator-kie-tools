package org.kie.uberfire.security.server;

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

}
