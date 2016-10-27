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
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AbstractClientSessionManagerTest {

    @Mock ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> readOnlySession;
    @Mock ClientFullSession<AbstractCanvas, AbstractCanvasHandler> fullSession;
    @Mock AbstractClientSession session;
    @Mock AbstractClientSession session1;

    private AbstractClientSessionManager tested;

    @Before
    public void setup() throws Exception {
        this.tested = spy( new AbstractClientSessionManager() {
            @Override
            protected void postOpen() {
            }

            @Override
            protected void postPause() {
            }

            @Override
            protected void postResume() {
            }

            @Override
            protected void postDispose() {
            }

            @Override
            public ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> newReadOnlySession() {
                return readOnlySession;
            }

            @Override
            public ClientFullSession<AbstractCanvas, AbstractCanvasHandler> newFullSession() {
                return fullSession;
            }
        } );
    }

    @Test
    public void testOpen() {
        tested.open( session );
        assertEquals( session, tested.getCurrentSession() );
        verify( session, times( 1 ) ).open();
        verify( session, times( 0 ) ).pause();
        verify( session, times( 0 ) ).resume();
        verify( session, times( 0 ) ).dispose();
        verify( tested, times( 1 ) ).postOpen();
        verify( tested, times( 0 ) ).postPause();
        verify( tested, times( 0 ) ).postResume();
        verify( tested, times( 0 ) ).postDispose();
    }

    @Test
    public void testOpenAnotherSession() {
        tested.current = session;
        tested.open( session1 );
        assertEquals( session1, tested.getCurrentSession() );
        verify( session, times( 1 ) ).pause();
        verify( session, times( 0 ) ).open();
        verify( session, times( 0 ) ).resume();
        verify( session, times( 0 ) ).dispose();
        verify( session1, times( 1 ) ).open();
        verify( session1, times( 0 ) ).pause();
        verify( session1, times( 0 ) ).resume();
        verify( session1, times( 0 ) ).dispose();
        verify( tested, times( 1 ) ).postOpen();
        verify( tested, times( 1 ) ).postPause();
        verify( tested, times( 0 ) ).postResume();
        verify( tested, times( 0 ) ).postDispose();
    }

    @Test
    public void testPause() {
        tested.current = session;
        tested.pause();
        assertEquals( session, tested.getCurrentSession() );
        verify( session, times( 0 ) ).open();
        verify( session, times( 1 ) ).pause();
        verify( session, times( 0 ) ).resume();
        verify( session, times( 0 ) ).dispose();
        verify( tested, times( 0 ) ).postOpen();
        verify( tested, times( 1 ) ).postPause();
        verify( tested, times( 0 ) ).postResume();
        verify( tested, times( 0 ) ).postDispose();
    }

    @Test
    public void testResume() {
        tested.current = session1;
        tested.resume( session );
        assertEquals( session, tested.getCurrentSession() );
        verify( session1, times( 0 ) ).open();
        verify( session1, times( 1 ) ).pause();
        verify( session1, times( 0 ) ).resume();
        verify( session1, times( 0 ) ).dispose();
        verify( session, times( 0 ) ).open();
        verify( session, times( 0 ) ).pause();
        verify( session, times( 1 ) ).resume();
        verify( session, times( 0 ) ).dispose();
        verify( tested, times( 0 ) ).postOpen();
        verify( tested, times( 1 ) ).postPause();
        verify( tested, times( 1 ) ).postResume();
        verify( tested, times( 0 ) ).postDispose();
    }

    @Test
    public void testDispose() {
        tested.current = session;
        tested.dispose();
        assertNull( tested.getCurrentSession() );
        verify( session, times( 0 ) ).open();
        verify( session, times( 0 ) ).pause();
        verify( session, times( 0 ) ).resume();
        verify( session, times( 1 ) ).dispose();
        verify( tested, times( 0 ) ).postOpen();
        verify( tested, times( 0 ) ).postPause();
        verify( tested, times( 0 ) ).postResume();
        verify( tested, times( 1 ) ).postDispose();
    }

}
