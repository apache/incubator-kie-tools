package org.uberfire.security.impl;

import java.io.Serializable;

import org.uberfire.security.Role;

public class RoleImpl implements Role,
                                 Serializable {

    private static final long serialVersionUID = 8713460024436782774L;

    private String name;

    public RoleImpl() {
    }

    public RoleImpl( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
