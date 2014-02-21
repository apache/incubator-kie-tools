package org.uberfire.security.server.mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.uberfire.security.Role;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;

/**
 * A simple role provider that claims every user belongs to the role "test_role".
 * 
 * @author jfuerth
 */
public class TestingRoleProvider implements RoleProvider {

    @Override
    public void initialize( Map<String, ?> options ) {
        System.out.println( "TestingRoleProvider got options: " + options );
    }

    @Override
    public List<Role> loadRoles( Principal principal ) {
        return Collections.singletonList( (Role) new RoleImpl( "test_role" ) );
    }

}
