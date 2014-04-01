package org.uberfire.security.impl;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.security.shared.api.Role;

@Portable
public class RoleImpl implements Role,
Serializable {

    private static final long serialVersionUID = 8713460024436782774L;

    private String name;

    public RoleImpl() {
    }

    public RoleImpl(@MapsTo("name") String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
