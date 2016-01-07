package org.uberfire.ext.security.management.wildfly;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.server.RolesRegistry;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WildflyRoleManagerTest {

    private WildflyRoleManager tested;

    @Before
    public void setup() {
        RolesRegistry.get().clear();
        tested = new WildflyRoleManager();
    }

    @Test
    public void testGetRegisteredRoles() {
        RolesRegistry.get().registerRole( "role1" );
        RolesRegistry.get().registerRole("role2");
        Set<Role> roles = tested.getRegisteredRoles();
        assertNotNull(roles);
        assertTrue(roles.size() == 3);
        assertTrue(roles.contains(new RoleImpl( "admin" )));
        assertTrue(roles.contains(new RoleImpl( "role1" )));
        assertTrue(roles.contains(new RoleImpl( "role2" )));
    }

}
