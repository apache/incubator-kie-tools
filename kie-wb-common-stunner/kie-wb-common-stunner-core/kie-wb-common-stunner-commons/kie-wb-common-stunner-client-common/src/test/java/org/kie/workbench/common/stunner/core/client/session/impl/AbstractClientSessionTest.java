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
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AbstractClientSessionTest {

    @Mock AbstractCanvas canvas;
    @Mock AbstractCanvasHandler canvasHandler;

    private AbstractClientSession tested;

    @Before
    public void setup() throws Exception {
        when( canvasHandler.getCanvas() ).thenReturn( canvas );
        this.tested = spy( new AbstractClientSessionStub( canvas, canvasHandler ) );
    }

    @Test
    public void testOpen() {
        tested.open();
        assertTrue( tested.isOpened() );
        verify( tested, times( 1 ) ).doOpen();
        verify( tested, times( 0 ) ).doPause();
        verify( tested, times( 0 ) ).doResume();
        verify( tested, times( 0 ) ).doDispose();
    }

    @Test
    public void testPause() {
        tested.isOpened = true;
        tested.pause();
        assertTrue( tested.isOpened() );
        verify( tested, times( 0 ) ).doOpen();
        verify( tested, times( 1 ) ).doPause();
        verify( tested, times( 0 ) ).doResume();
        verify( tested, times( 0 ) ).doDispose();
    }

    @Test( expected = java.lang.IllegalStateException.class )
    public void testCannotPause() {
        tested.isOpened = false;
        tested.pause();
    }

    @Test
    public void testResume() {
        tested.isOpened = true;
        tested.resume();
        assertTrue( tested.isOpened() );
        verify( tested, times( 0 ) ).doOpen();
        verify( tested, times( 0 ) ).doPause();
        verify( tested, times( 1 ) ).doResume();
        verify( tested, times( 0 ) ).doDispose();
    }

    @Test( expected = java.lang.IllegalStateException.class )
    public void testCannotResume() {
        tested.isOpened = false;
        tested.resume();
    }

    @Test
    public void testDispose() {
        tested.isOpened = true;
        tested.dispose();
        assertFalse( tested.isOpened() );
        verify( tested, times( 0 ) ).doOpen();
        verify( tested, times( 0 ) ).doPause();
        verify( tested, times( 0 ) ).doResume();
        verify( tested, times( 1 ) ).doDispose();
        verify( canvasHandler, times( 1 ) ).destroy();
    }

    @Test( expected = java.lang.IllegalStateException.class )
    public void testCannotDispose() {
        tested.isOpened = false;
        tested.dispose();
    }

    private class AbstractClientSessionStub extends AbstractClientSession {

        AbstractClientSessionStub( AbstractCanvas canvas,
                                   AbstractCanvasHandler canvasHandler ) {
            super( canvas, canvasHandler );
        }

        @Override
        protected void doOpen() {
        }

        @Override
        protected void doPause() {
        }

        @Override
        protected void doResume() {
        }

        @Override
        protected void doDispose() {
        }
    }

}
