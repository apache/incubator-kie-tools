package org.uberfire.security.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.uberfire.security.Identity;
import org.uberfire.security.Role;

import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

public class IdentityImpl implements Identity,
                                     Serializable {

    private static final long serialVersionUID = 3172905561115755369L;

    private final List<Role> roles = new ArrayList<Role>();
    private String name;

    public IdentityImpl() {
    }

    public IdentityImpl( final String name,
                         final List<Role> roles ) {
        this.name = name;
        this.roles.addAll( roles );
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean hasRole( final Role role ) {
        checkNotNull( "role", role );
        for ( final Role activeRole : roles ) {
            if ( activeRole.getName().equals( role.getName() ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }
}
