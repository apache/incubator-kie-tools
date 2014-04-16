package org.uberfire.security.impl.authz;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeResource;

public class RuntimeAuthorizationManagerTest {

    @Test
    public void testAuthorizeWithCacheRefreshOnRemoveAllRoles() {
        RuntimeAuthorizationManager authorizationManager = new RuntimeAuthorizationManager();

        RuntimeResource resource = new TestRuntimeResource("test1234", "author");
        User john = new TestIdentity("john", "admin");
        User mary = new TestIdentity("mary", "author");

        assertTrue(resource instanceof Cacheable);
        assertTrue(((Cacheable) resource).requiresRefresh());

        boolean authorized = authorizationManager.authorize(resource, john);
        assertFalse(authorized);
        assertFalse(((Cacheable) resource).requiresRefresh());

        authorized = authorizationManager.authorize(resource, mary);
        assertTrue(authorized);
        // now simulate remove of the roles for the resource
        RuntimeResource resource2 = new TestRuntimeResource("test1234", (String[]) null);

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
        User john = new TestIdentity("john", "admin");
        User mary = new TestIdentity("mary", "author");

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
        User john = new TestIdentity("john", "admin");
        User mary = new TestIdentity("mary", "author");

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

        private final String signatureId;
        private List<String> roles;
        private boolean requiresRefresh = true;

        protected TestRuntimeResource(String signatureId, String... roles) {
            this.signatureId = signatureId;
            if (roles != null) {
                this.roles = Arrays.asList(roles);
            } else {
                this.roles = Collections.emptyList();
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

    private class TestIdentity implements User {

        private final String name;
        private final Set<Role> roles;

        protected TestIdentity(String name, String... rolesIn) {
            this.name = name;
            this.roles = new HashSet<Role>();

            for (String role : rolesIn) {
                roles.add(new RoleImpl(role));
            }
        }

        @Override
        public String getIdentifier() {
            return name;
        }

        @Override
        public Set<Role> getRoles() {
            return roles;
        }

        @Override
        public boolean hasAllRoles(String ... roleNames) {
            for (String roleName : roleNames) {
                boolean foundThisOne = false;
                for (Role role : roles) {
                    if (roleName.equals(role.getName())) {
                        foundThisOne = true;
                        break;
                    }
                }
                if (!foundThisOne) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean hasAnyRoles(String ... roleNames) {
            for (Role role : roles) {
                for (String roleName : roleNames) {
                    if (roleName.equals(role.getName())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public void removeProperty( String name ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void setProperty( String name, String value ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public String getProperty( String name ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

    }
}
