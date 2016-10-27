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

package org.kie.workbench.common.stunner.core.client.session.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.platform.PlatformManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ClientSessionManagerImplTest {

    @Mock PlatformManager platformManager;
    @Mock EventSourceMock<SessionOpenedEvent> sessionOpenedEventMock;
    @Mock EventSourceMock<SessionPausedEvent> sessionPausedEventMock;
    @Mock EventSourceMock<SessionResumedEvent> sessionResumedEventMock;
    @Mock EventSourceMock<SessionDisposedEvent> sessionDisposedEventMock;
    @Mock AbstractClientSession session;
    @Mock AbstractClientSession session1;

    private ClientSessionManagerImpl tested;

    @Before
    public void setup() throws Exception {
        this.tested = new ClientSessionManagerImpl( platformManager, sessionOpenedEventMock,
                sessionDisposedEventMock, sessionPausedEventMock, sessionResumedEventMock );
    }

    @Test
    public void testPostOpen() {
        tested.open( session );
        verify( sessionOpenedEventMock, times( 1 ) ).fire( any( SessionOpenedEvent.class ) );
        verify( sessionPausedEventMock, times( 0 ) ).fire( any( SessionPausedEvent.class ) );
        verify( sessionResumedEventMock, times( 0 ) ).fire( any( SessionResumedEvent.class ) );
        verify( sessionDisposedEventMock, times( 0 ) ).fire( any( SessionDisposedEvent.class ) );
    }

    @Test
    public void testPostOpenAnotherSession() {
        tested.current = session;
        tested.open( session1 );
        verify( sessionOpenedEventMock, times( 1 ) ).fire( any( SessionOpenedEvent.class ) );
        verify( sessionPausedEventMock, times( 1 ) ).fire( any( SessionPausedEvent.class ) );
        verify( sessionResumedEventMock, times( 0 ) ).fire( any( SessionResumedEvent.class ) );
        verify( sessionDisposedEventMock, times( 0 ) ).fire( any( SessionDisposedEvent.class ) );
    }

    @Test
    public void testPostPause() {
        tested.current = session;
        tested.pause();
        verify( sessionOpenedEventMock, times( 0 ) ).fire( any( SessionOpenedEvent.class ) );
        verify( sessionPausedEventMock, times( 1 ) ).fire( any( SessionPausedEvent.class ) );
        verify( sessionResumedEventMock, times( 0 ) ).fire( any( SessionResumedEvent.class ) );
        verify( sessionDisposedEventMock, times( 0 ) ).fire( any( SessionDisposedEvent.class ) );
    }

    @Test
    public void testPostResume() {
        tested.current = session1;
        tested.resume( session );
        verify( sessionOpenedEventMock, times( 0 ) ).fire( any( SessionOpenedEvent.class ) );
        verify( sessionPausedEventMock, times( 1 ) ).fire( any( SessionPausedEvent.class ) );
        verify( sessionResumedEventMock, times( 1 ) ).fire( any( SessionResumedEvent.class ) );
        verify( sessionDisposedEventMock, times( 0 ) ).fire( any( SessionDisposedEvent.class ) );
    }

    @Test
    public void testPostDispose() {
        tested.current = session;
        tested.dispose();
        verify( sessionOpenedEventMock, times( 0 ) ).fire( any( SessionOpenedEvent.class ) );
        verify( sessionPausedEventMock, times( 0 ) ).fire( any( SessionPausedEvent.class ) );
        verify( sessionResumedEventMock, times( 0 ) ).fire( any( SessionResumedEvent.class ) );
        verify( sessionDisposedEventMock, times( 1 ) ).fire( any( SessionDisposedEvent.class ) );
    }

}
