package org.uberfire.security.impl;

import static java.util.Collections.unmodifiableMap;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.security.Identity;
import org.uberfire.security.Role;

public class IdentityImpl implements Identity,
                                     Serializable {

    private static final long serialVersionUID = 3172905561115755369L;

    private final List<Role> roles = new ArrayList<Role>();
    private String name;
    private final Map<String, String> properties = new HashMap<String, String>();

    public IdentityImpl() {
    }

    public IdentityImpl( final String name ) {
        this( name, Collections.<Role>emptyList() );
    }

    public IdentityImpl( final String name,
                         final List<Role> roles ) {
        this.name = name;
        this.roles.addAll( roles );
    }

    public IdentityImpl( final String name,
                         final List<Role> roles,
                         final Map<String, String> properties ) {
        this.name = name;
        this.roles.addAll( roles );
        this.properties.putAll( properties );
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
    public Map<String, String> getProperties() {
        return unmodifiableMap( properties );
    }

    @Override
    public void aggregateProperty( final String name,
                                   final String value ) {
        properties.put( name, value );
    }

    @Override
    public void removeProperty( final String name ) {
        properties.remove( name );
    }

    @Override
    public String getProperty( final String name,
                               final String defaultValue ) {
        final String result = properties.get( name );
        if ( result == null ) {
            return defaultValue;
        }
        return result;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Identity ) ) {
            return false;
        }

        Identity identity = (Identity) o;

        return name.equals( identity.getName() );
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
      return "IdentityImpl [roles=" + roles + ", name=" + name + ", properties=" + properties + "]";
    }

}
