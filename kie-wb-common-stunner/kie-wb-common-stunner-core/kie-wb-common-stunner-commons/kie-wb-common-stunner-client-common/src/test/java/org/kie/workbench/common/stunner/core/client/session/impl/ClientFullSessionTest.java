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
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasNameEditionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.palette.CanvasPaletteControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ClientFullSessionTest {

    @Mock AbstractCanvas canvas;
    @Mock AbstractCanvasHandler canvasHandler;
    @Mock SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    @Mock ZoomControl<AbstractCanvas> zoomControl;
    @Mock PanControl<AbstractCanvas> panControl;
    @Mock ResizeControl<AbstractCanvasHandler, Element> resizeControl;
    @Mock CanvasValidationControl<AbstractCanvasHandler> canvasValidationControl;
    @Mock CanvasPaletteControl<AbstractCanvasHandler> canvasPaletteControl;
    @Mock CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    @Mock ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    @Mock ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    @Mock DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    @Mock CanvasNameEditionControl<AbstractCanvasHandler, Element> canvasNameEditionControl;
    @Mock DragControl<AbstractCanvasHandler, Element> dragControl;
    @Mock ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;
    @Mock ElementBuilderControl<AbstractCanvasHandler> builderControl;

    private ClientFullSessionImpl tested;

    @Before
    public void setup() throws Exception {
        when( canvasHandler.getCanvas() ).thenReturn( canvas );
        this.tested = new ClientFullSessionImpl( canvas, canvasHandler, resizeControl, canvasValidationControl,
                canvasPaletteControl, canvasCommandManager, connectionAcceptorControl, containmentAcceptorControl,
                dockingAcceptorControl, canvasNameEditionControl, selectionControl, dragControl, toolboxControl,
                builderControl, zoomControl, panControl );
    }

    @Test
    public void testInit() {
        assertEquals( canvas, tested.getCanvas() );
        assertEquals( canvasHandler, tested.getCanvasHandler() );
        assertEquals( selectionControl, tested.getSelectionControl() );
        assertEquals( zoomControl, tested.getZoomControl() );
        assertEquals( panControl, tested.getPanControl() );
        assertEquals( resizeControl, tested.getResizeControl() );
        assertEquals( canvasValidationControl, tested.getCanvasValidationControl() );
        assertEquals( canvasPaletteControl, tested.getCanvasPaletteControl() );
        assertEquals( canvasCommandManager, tested.getCanvasCommandManager() );
        assertEquals( connectionAcceptorControl, tested.getConnectionAcceptorControl() );
        assertEquals( containmentAcceptorControl, tested.getContainmentAcceptorControl() );
        assertEquals( dockingAcceptorControl, tested.getDockingAcceptorControl() );
        assertEquals( canvasNameEditionControl, tested.getCanvasNameEditionControl() );
        assertEquals( dragControl, tested.getDragControl() );
        assertEquals( toolboxControl, tested.getToolboxControl() );
        assertEquals( builderControl, tested.getBuilderControl() );
    }

    @Test
    public void testOpenSession() {
        tested.open();
        verify( canvas, times( 1 ) ).addRegistrationListener( any( CanvasShapeListener.class ) );
        verify( canvasHandler, times( 1 ) ).addRegistrationListener( any( CanvasElementListener.class ) );
        verify( selectionControl, times( 1 ) ).enable( eq( canvasHandler  ) );
        verify( zoomControl, times( 1 ) ).enable( eq( canvas ) );
        verify( panControl, times( 1 ) ).enable( eq( canvas ) );
        verify( resizeControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( canvasValidationControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( canvasPaletteControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( connectionAcceptorControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( containmentAcceptorControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( dockingAcceptorControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( canvasNameEditionControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( dragControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( toolboxControl, times( 1 ) ).enable( eq( canvasHandler ) );
        verify( builderControl, times( 1 ) ).enable( eq( canvasHandler ) );
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
        verify( resizeControl, times( 1 ) ).disable();
        verify( canvasValidationControl, times( 1 ) ).disable();
        verify( canvasPaletteControl, times( 1 ) ).disable();
        verify( connectionAcceptorControl, times( 1 ) ).disable();
        verify( containmentAcceptorControl, times( 1 ) ).disable();
        verify( dockingAcceptorControl, times( 1 ) ).disable();
        verify( canvasNameEditionControl, times( 1 ) ).disable();
        verify( dragControl, times( 1 ) ).disable();
        verify( toolboxControl, times( 1 ) ).disable();
        verify( builderControl, times( 1 ) ).disable();
    }

}
