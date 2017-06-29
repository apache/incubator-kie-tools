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

package org.uberfire.backend.server.cdi.workspace;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workspace.WorkspaceContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceNameResolverTest {

    private WorkspaceNameResolver resolver;
    private BeanManager beanManager;

    @Before
    public void setUp() {
        WorkspaceContext.set(null);
        beanManager = mock(BeanManager.class);
        resolver = Mockito.spy(new WorkspaceNameResolver(beanManager));
    }

    @Test
    public void testGlobalWorkspaceName() {
        String name = this.resolver.getWorkspaceName();
        assertEquals(WorkspaceNameResolver.GLOBAL_WORKSPACE_NAME,
                     name);
    }

    @Test
    public void testContextWorkspaceName() {
        final String user = "hendrix";
        WorkspaceContext.set(user);
        String name = this.resolver.getWorkspaceName();
        assertEquals(user,
                     name);
    }

    @Test
    public void testUserInfoWorkspaceName() {
        final String user = "clapton";

        SessionInfo sessionInfo = mock(SessionInfo.class);
        when(sessionInfo.getIdentity()).thenReturn(new UserImpl(user));
        doReturn(sessionInfo).when(this.resolver).getSessionInfo();
        String name = this.resolver.getWorkspaceName();
        assertEquals(user,
                     name);
    }
}