package org.uberfire.security.server.auth.source.adapter;

import java.util.List;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RolesMode;

public interface RolesAdapter {

    List<Role> getRoles( final Principal principal,
                         final SecurityContext securityContext,
                         final RolesMode mode );
}
