/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.api;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.project.client.screens.ProjectDiagramWorkbenchDocks;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ClientProjectSessionManagerTest {

    @Mock
    DefinitionUtils definitionUtils;
    @Mock
    ManagedInstance<ClientSessionFactory> sessionFactoriesInstances;
    @Mock
    ProjectDiagramWorkbenchDocks editorDocks;
    @Mock
    EventSourceMock<SessionOpenedEvent> sessionOpenedEvent;
    @Mock
    EventSourceMock<SessionDestroyedEvent> sessionDestroyedEvent;
    @Mock
    EventSourceMock<SessionPausedEvent> sessionPausedEvent;
    @Mock
    EventSourceMock<SessionResumedEvent> sessionResumedEvent;
    @Mock
    EventSourceMock<OnSessionErrorEvent> sessionErrorEvent;
    @Mock
    AbstractClientSession session;

    private ClientProjectSessionManager tested;

    @Before
    public void setup() throws Exception {
        this.tested = new ClientProjectSessionManager(definitionUtils,
                                                      editorDocks,
                                                      sessionFactoriesInstances,
                                                      sessionOpenedEvent,
                                                      sessionDestroyedEvent,
                                                      sessionPausedEvent,
                                                      sessionResumedEvent,
                                                      sessionErrorEvent);
    }

    @Test
    public void testOpen() {
        tested.open(session);
        verify(editorDocks,
               times(1)).enableDocks();
        verify(editorDocks,
               times(0)).disableDocks();
    }

    @Test
    public void testResume() {
        tested.resume(session);
        verify(editorDocks,
               times(1)).enableDocks();
        verify(editorDocks,
               times(0)).disableDocks();
    }

    @Test
    public void testDestroy() {
        tested.postDestroy();
        verify(editorDocks,
               times(0)).enableDocks();
        verify(editorDocks,
               times(1)).disableDocks();
    }
}
