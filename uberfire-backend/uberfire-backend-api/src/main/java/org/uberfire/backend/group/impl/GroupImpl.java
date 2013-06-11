package org.uberfire.backend.group.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

@Portable
public class GroupImpl implements Group {

    private String name;
    private String owner;

    private Collection<Repository> repositories = new ArrayList<Repository>();
    private Collection<String> roles = new ArrayList<String>();

    public GroupImpl() {
    }

    public GroupImpl( String name,
                      String owner ) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public Collection<Repository> getRepositories() {
        return repositories;
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + getName();
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptySet();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof GroupImpl ) ) {
            return false;
        }

        GroupImpl group = (GroupImpl) o;

        if ( name != null ? !name.equals( group.name ) : group.name != null ) {
            return false;
        }
        if ( owner != null ? !owner.equals( group.owner ) : group.owner != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( owner != null ? owner.hashCode() : 0 );
        return result;
    }
}
