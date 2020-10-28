/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
 */

package org.uberfire.server;

import javax.enterprise.inject.Instance;

import org.jboss.errai.bus.client.api.QueueSession;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.server.cdi.UberFireGeneralFactory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UberFireGeneralFactoryTest {

    @Mock
    private Instance<User> userInstance;

    @Mock
    private AuthenticationService authService;

    @Mock
    private Message threadMessage;

    @Mock
    private QueueSession threadQueueSession;

    private User sessionUser = new UserImpl("session");

    private User defaultUser = new UserImpl("default");

    @InjectMocks
    private UberFireGeneralFactory factory;

    @Before
    public void setup() {
        when(threadMessage.getResource(QueueSession.class,
                                       "Session")).thenReturn(threadQueueSession);
        when(threadQueueSession.getSessionId()).thenReturn(sessionUser.getIdentifier());
        RpcContext.set(null);
    }

    @Test
    public void returnDefaultUserOutsideOfSessionThread() {
        when(userInstance.isAmbiguous()).thenReturn(false);
        when(userInstance.isUnsatisfied()).thenReturn(false);
        when(userInstance.get()).thenReturn(defaultUser);

        SessionInfo sessionInfo = factory.getSessionInfo(authService);
        assertSame(defaultUser,
                   sessionInfo.getIdentity());
    }

    @Test
    public void returnAuthenticatedUserInSessionThread() {
        reset(authService);
        when(authService.getUser()).thenReturn(sessionUser);
        RpcContext.set(threadMessage);

        SessionInfo sessionInfo = factory.getSessionInfo(authService);
        assertSame(sessionUser,
                   sessionInfo.getIdentity());
    }

    @Test
    public void throwIllegalStateExceptionOutsideOfSessionThreadWithoutDefaultUser() {
        when(userInstance.isAmbiguous()).thenReturn(false);
        when(userInstance.isUnsatisfied()).thenReturn(true);

        assertThatThrownBy(() -> factory.getSessionInfo(authService))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot get session info outside of servlet thread when no default user is provided.");
    }
}
