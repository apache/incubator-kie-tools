package org.uberfire.security.impl.authz;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

import com.google.common.collect.ImmutableSet;

public class RuntimeAuthorizationManagerTest {

    @Test
    public void testAuthorizeWithCacheRefreshOnRemoveAllRoles() {
        RuntimeAuthorizationManager authorizationManager = new RuntimeAuthorizationManager();

        RuntimeResource resource = new TestRuntimeResource("test1234", "author");
        User john = new UserImpl( "john", ImmutableSet.of( new RoleImpl( "admin" ) ) );
        User mary = new UserImpl( "mary", ImmutableSet.of( new RoleImpl( "author") ) );

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
        User john = new UserImpl( "john", ImmutableSet.of( new RoleImpl( "admin" ) ) );
        User mary = new UserImpl( "mary", ImmutableSet.of( new RoleImpl( "author") ) );

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
        User john = new UserImpl( "john", ImmutableSet.of( new RoleImpl( "admin" ) ) );
        User mary = new UserImpl( "mary", ImmutableSet.of( new RoleImpl( "author") ) );

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


    private class TestRuntimeResource implements RuntimeFeatureResource, Cacheable {

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
}
