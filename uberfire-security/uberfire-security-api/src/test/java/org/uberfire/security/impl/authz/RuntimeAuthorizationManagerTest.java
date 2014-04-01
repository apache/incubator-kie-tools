package org.uberfire.security.impl.authz;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.security.Subject;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.commons.data.Cacheable;

public class RuntimeAuthorizationManagerTest {

    @Test
    public void testAuthorizeWithCacheRefreshOnRemoveAllRoles() {
        RuntimeAuthorizationManager authorizationManager = new RuntimeAuthorizationManager();

        RuntimeResource resource = new TestRuntimeResource("test1234", "author");
        Subject john = new TestIdentity("john", "admin");
        Subject mary = new TestIdentity("mary", "author");

        assertTrue(resource instanceof Cacheable);
        assertTrue(((Cacheable) resource).requiresRefresh());

        boolean authorized = authorizationManager.authorize(resource, john);
        assertFalse(authorized);
        assertFalse(((Cacheable) resource).requiresRefresh());

        authorized = authorizationManager.authorize(resource, mary);
        assertTrue(authorized);
        // now simulate remove of the roles for the resource
        RuntimeResource resource2 = new TestRuntimeResource("test1234",  null);

        assertTrue(((Cacheable) resource2).requiresRefresh());

        authorized = authorizationManager.authorize(resource2, john);
        assertTrue(authorized);

        authorized = authorizationManager.authorize(resource2, mary);
        assertTrue(authorized);
    }

    @Test
    public void testAuthorizeWithCacheRefreshOnAddedRole() {
        RuntimeAuthorizationManager authorizationManager = new RuntimeAuthorizationManager();

        RuntimeResource resource = new TestRuntimeResource("test1234", "author");
        Subject john = new TestIdentity("john", "admin");
        Subject mary = new TestIdentity("mary", "author");

        assertTrue(resource instanceof Cacheable);
        assertTrue(((Cacheable) resource).requiresRefresh());

        boolean authorized = authorizationManager.authorize(resource, john);
        assertFalse(authorized);
        assertFalse(((Cacheable) resource).requiresRefresh());

        authorized = authorizationManager.authorize(resource, mary);
        assertTrue(authorized);
        // now simulate add of a role for the resource
        RuntimeResource resource2 = new TestRuntimeResource("test1234",  "admin", "author");

        assertTrue(((Cacheable) resource2).requiresRefresh());

        authorized = authorizationManager.authorize(resource2, john);
        assertTrue(authorized);
        assertFalse(((Cacheable) resource2).requiresRefresh());

        authorized = authorizationManager.authorize(resource2, mary);
        assertTrue(authorized);
    }

    @Test
    public void testAuthorizeWithCacheRefreshOnRemovedRole() {
        RuntimeAuthorizationManager authorizationManager = new RuntimeAuthorizationManager();

        RuntimeResource resource = new TestRuntimeResource("test1234", "admin", "author");
        Subject john = new TestIdentity("john", "admin");
        Subject mary = new TestIdentity("mary", "author");

        assertTrue(resource instanceof Cacheable);
        assertTrue(((Cacheable) resource).requiresRefresh());

        boolean authorized = authorizationManager.authorize(resource, john);
        assertTrue(authorized);
        assertFalse(((Cacheable) resource).requiresRefresh());

        authorized = authorizationManager.authorize(resource, mary);
        assertTrue(authorized);
        // now simulate remove of a role for the resource
        RuntimeResource resource2 = new TestRuntimeResource("test1234", "author");

        assertTrue(((Cacheable) resource2).requiresRefresh());

        authorized = authorizationManager.authorize(resource2, john);
        assertFalse(authorized);
        assertFalse(((Cacheable) resource2).requiresRefresh());

        authorized = authorizationManager.authorize(resource2, mary);
        assertTrue(authorized);
    }


    private class TestRuntimeResource implements RuntimeResource, Cacheable {

        private String signatureId;
        private List<String> roles;
        private boolean requiresRefresh = true;

        protected TestRuntimeResource(String signatureId, String... roles) {
            this.signatureId = signatureId;
            if (roles != null) {
                this.roles = Arrays.asList(roles);
            } else {
                this.roles = Collections.EMPTY_LIST;
            }
        }

        @Override
        public String getSignatureId() {
            return this.signatureId;
        }

        @Override
        public Collection<String> getRoles() {
            return this.roles;
        }

        @Override
        public Collection<String> getTraits() {
            return Collections.emptySet();
        }

        @Override
        public void markAsCached() {
            this.requiresRefresh = false;
        }

        @Override
        public boolean requiresRefresh() {
            return requiresRefresh;
        }
    }

    private class TestIdentity implements Identity {

        private String name;
        private List<Role> roles;

        protected TestIdentity(String name, String... rolesIn) {
            this.name = name;
            this.roles = new ArrayList<Role>();

            for (String role : rolesIn) {
                roles.add(new RoleImpl(role));
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Role> getRoles() {
            return roles;
        }

        @Override
        public boolean hasRole( Role role ) {
            return roles.contains(role);
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public void aggregateProperty( String name,
                String value ) {
        }

        @Override
        public void removeProperty( String name ) {
        }

        @Override
        public String getProperty( String name,
                String defaultValue ) {
            return null;
        }
    }
}
