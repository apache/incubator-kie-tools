/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.session.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.platform.PlatformManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.project.client.screens.ProjectDiagramWorkbenchDocks;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ClientProjectSessionManagerTest {

    @Mock
    ProjectDiagramWorkbenchDocks editorDocks;
    @Mock
    PlatformManager platformManager;
    @Mock
    EventSourceMock<SessionOpenedEvent> sessionOpenedEvent;
    @Mock
    EventSourceMock<SessionDisposedEvent> sessionDisposedEvent;
    @Mock
    EventSourceMock<SessionPausedEvent> sessionPausedEvent;
    @Mock
    EventSourceMock<SessionResumedEvent> sessionResumedEvent;
    @Mock
    AbstractClientSession session;

    private ClientProjectSessionManager tested;

    @Before
    public void setup() throws Exception {
        this.tested = new ClientProjectSessionManager( editorDocks, platformManager,
                sessionOpenedEvent, sessionDisposedEvent, sessionPausedEvent, sessionResumedEvent );
    }

    @Test
    public void testOpen() {
        tested.open( session );
        verify( editorDocks, times( 1 ) ).enableDocks();
        verify( editorDocks, times( 0 ) ).disableDocks();
    }

    @Test
    public void testResume() {
        tested.resume( session );
        verify( editorDocks, times( 1 ) ).enableDocks();
        verify( editorDocks, times( 0 ) ).disableDocks();
    }

    @Test
    public void testDispose() {
        tested.postDispose();
        verify( editorDocks, times( 0 ) ).enableDocks();
        verify( editorDocks, times( 1 ) ).disableDocks();
    }

}
