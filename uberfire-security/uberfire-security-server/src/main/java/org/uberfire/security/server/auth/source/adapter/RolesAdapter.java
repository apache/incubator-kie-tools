package org.uberfire.security.server.auth.source.adapter;

import java.util.List;

import org.jboss.errai.security.shared.api.Role;

public interface RolesAdapter {

    List<Role> getRoles(String username);
}
