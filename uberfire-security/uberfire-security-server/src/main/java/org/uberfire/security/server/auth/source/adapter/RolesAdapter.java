package org.uberfire.security.server.auth.source.adapter;

import java.util.List;

import org.uberfire.security.Role;

public interface RolesAdapter {

    List<Role> getRoles(String username);
}
