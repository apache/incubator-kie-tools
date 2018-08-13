/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.ext.security.server;

import javax.enterprise.inject.Instance;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import com.google.common.collect.ImmutableSet;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CleanupSecurityCacheSessionListenerTest {

    @Mock
    private HttpSessionEvent evt;

    @Mock
    private HttpSession session;

    @Test
    public void testSessionCleanup() {
        final AuthorizationManager authorizationManager1 = mock(AuthorizationManager.class);
        final AuthorizationManager authorizationManager2 = mock(AuthorizationManager.class);

        final Instance<AuthorizationManager> instances = mock(Instance.class);
        when(instances.iterator()).thenReturn(asList(authorizationManager1,
                                                     authorizationManager2).iterator());

        final CleanupSecurityCacheSessionListener listener = new CleanupSecurityCacheSessionListener(instances);

        final User user = new UserImpl("user", ImmutableSet.of(new RoleImpl("author")));

        when(evt.getSession()).thenReturn(session);
        when(session.getAttribute(ServletSecurityAuthenticationService.USER_SESSION_ATTR_NAME)).thenReturn(user);

        listener.sessionDestroyed(evt);

        verify(authorizationManager1, times(1)).invalidate(user);
        verify(authorizationManager2, times(1)).invalidate(user);
    }

    @Test
    public void testSessionCleanupNPE() {
        final CleanupSecurityCacheSessionListener listener = new CleanupSecurityCacheSessionListener();

        final User user = new UserImpl("user", ImmutableSet.of(new RoleImpl("author")));

        when(evt.getSession()).thenReturn(session);
        when(session.getAttribute(ServletSecurityAuthenticationService.USER_SESSION_ATTR_NAME)).thenReturn(user);

        listener.sessionDestroyed(evt);
    }
}
