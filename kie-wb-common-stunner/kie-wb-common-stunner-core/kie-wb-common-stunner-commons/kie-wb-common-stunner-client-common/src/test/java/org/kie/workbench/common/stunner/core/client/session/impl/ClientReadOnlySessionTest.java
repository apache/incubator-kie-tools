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
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ClientReadOnlySessionTest {

    @Mock AbstractCanvas canvas;
    @Mock AbstractCanvasHandler canvasHandler;
    @Mock SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    @Mock ZoomControl<AbstractCanvas> zoomControl;
    @Mock PanControl<AbstractCanvas> panControl;

    private ClientReadOnlySessionImpl tested;

    @Before
    public void setup() throws Exception {
        when( canvasHandler.getCanvas() ).thenReturn( canvas );
        this.tested = new ClientReadOnlySessionImpl( canvas, canvasHandler, selectionControl,
                zoomControl, panControl );
    }

    @Test
    public void testInit() {
        assertEquals( canvas, tested.getCanvas() );
        assertEquals( canvasHandler, tested.getCanvasHandler() );
        assertEquals( selectionControl, tested.getSelectionControl() );
        assertEquals( zoomControl, tested.getZoomControl() );
        assertEquals( panControl, tested.getPanControl() );
    }

    @Test
    public void testOpenSession() {
        tested.open();
        verify( canvas, times( 1 ) ).addRegistrationListener( any( CanvasShapeListener.class ) );
        verify( canvasHandler, times( 1 ) ).addRegistrationListener( any( CanvasElementListener.class ) );
        verify( selectionControl, times( 1 ) ).enable( eq( canvasHandler  ) );
        verify( zoomControl, times( 1 ) ).enable( eq( canvas ) );
        verify( panControl, times( 1 ) ).enable( eq( canvas ) );
    }

    @Test
    public void testDisposeSession() {
        tested.isOpened = true;
        tested.doOpen(); // Force to register listeners.
        tested.dispose();
        assertFalse( tested.isOpened() );
        verify( canvas, times( 1 ) ).removeRegistrationListener( any( CanvasShapeListener.class ) );
        verify( canvasHandler, times( 1 ) ).removeRegistrationListener( any( CanvasElementListener.class ) );
        verify( canvasHandler, times( 1 ) ).destroy();
        verify( selectionControl, times( 1 ) ).disable();
        verify( zoomControl, times( 1 ) ).disable();
        verify( panControl, times( 1 ) ).disable();
    }

}
