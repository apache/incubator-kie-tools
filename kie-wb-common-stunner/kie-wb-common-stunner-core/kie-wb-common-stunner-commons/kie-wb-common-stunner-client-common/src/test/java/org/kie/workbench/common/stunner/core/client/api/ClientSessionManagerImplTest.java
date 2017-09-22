/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.api;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ClientSessionManagerImplTest {

    @Mock
    DefinitionUtils definitionUtils;
    @Mock
    ManagedInstance<ClientSessionFactory> sessionFactoriesInstances;
    @Mock
    EventSourceMock<SessionOpenedEvent> sessionOpenedEventMock;
    @Mock
    EventSourceMock<SessionPausedEvent> sessionPausedEventMock;
    @Mock
    EventSourceMock<SessionResumedEvent> sessionResumedEventMock;
    @Mock
    EventSourceMock<SessionDestroyedEvent> sessionDestroyedEventMock;
    @Mock
    EventSourceMock<OnSessionErrorEvent> sessionErrorEventMock;
    @Mock
    AbstractClientSession session;
    @Mock
    AbstractClientSession session1;

    private ClientSessionManagerImpl tested;

    @Before
    public void setup() throws Exception {
        this.tested = new ClientSessionManagerImpl(definitionUtils,
                                                   sessionFactoriesInstances,
                                                   sessionOpenedEventMock,
                                                   sessionDestroyedEventMock,
                                                   sessionPausedEventMock,
                                                   sessionResumedEventMock,
                                                   sessionErrorEventMock);
    }

    @Test
    public void testPostOpen() {
        tested.open(session);
        verify(sessionOpenedEventMock,
               times(1)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostOpenAnotherSession() {
        tested.current = session;
        tested.open(session1);
        verify(sessionOpenedEventMock,
               times(1)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(1)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostPause() {
        tested.current = session;
        tested.pause();
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(1)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostResume() {
        tested.current = session1;
        tested.resume(session);
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(1)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(1)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostDestroy() {
        tested.current = session;
        tested.destroy();
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(1)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testHandleClientError() {
        tested.current = session;
        ClientRuntimeError error = mock(ClientRuntimeError.class);
        tested.handleClientError(error);
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(1)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testHandleClientCommandError() {
        tested.current = session;
        CommandException error = mock(CommandException.class);
        tested.handleCommandError(error);
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(1)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }
}
