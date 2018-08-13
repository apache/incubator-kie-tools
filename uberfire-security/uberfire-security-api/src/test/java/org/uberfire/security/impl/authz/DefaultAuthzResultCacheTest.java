package org.uberfire.security.impl.authz;

import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultAuthzResultCacheTest {

    protected User createUserMock(String... roles) {
        return new UserImpl("username",
                             Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet()),
                             Collections.emptyList());
    }

    @Test
    public void testInvalidate() {
        final User user = createUserMock("admin");

        final Permission viewAll = new DotNamedPermission("resource.read", true);

        DefaultAuthzResultCache cache = new DefaultAuthzResultCache();
        cache.put(user, viewAll, AuthorizationResult.ACCESS_GRANTED);

        assertEquals(AuthorizationResult.ACCESS_GRANTED, cache.get(user, viewAll));

        cache.invalidate(user);

        assertNull(cache.get(user, viewAll));
    }

}
