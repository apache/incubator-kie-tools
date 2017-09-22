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

package org.kie.workbench.common.stunner.cm.client.session;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CaseManagementClientFullSessionTest {

    @Mock
    private CanvasFactory<AbstractCanvas, AbstractCanvasHandler> factory;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private SelectionControl<AbstractCanvasHandler, Element> selectionControl;

    @Mock
    private ZoomControl<AbstractCanvas> zoomControl;

    @Mock
    private PanControl<AbstractCanvas> panControl;

    @Mock
    private ResizeControl<AbstractCanvasHandler, Element> resizeControl;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> requestCommandManager;

    @Mock
    private RegistryFactory registryFactory;

    @Mock
    private ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;

    @Mock
    private ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;

    @Mock
    private DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;

    @Mock
    private CanvasInPlaceTextEditorControl<AbstractCanvasHandler, ClientSession, Element> canvasInPlaceTextEditorControl;

    @Mock
    private ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;

    @Mock
    private ElementBuilderControl<AbstractCanvasHandler> builderControl;

    @Mock
    private KeyboardControl keyboardControl;

    private CaseManagementClientFullSession session;

    @Before
    public void setup() throws Exception {
        when(factory.newCanvas()).thenReturn(canvas);
        when(factory.newCanvasHandler()).thenReturn(canvasHandler);
        when(factory.newControl(eq(ZoomControl.class))).thenReturn(zoomControl);
        when(factory.newControl(eq(PanControl.class))).thenReturn(panControl);
        when(factory.newControl(eq(SelectionControl.class))).thenReturn(selectionControl);
        when(factory.newControl(eq(ResizeControl.class))).thenReturn(resizeControl);
        when(factory.newControl(eq(ConnectionAcceptorControl.class))).thenReturn(connectionAcceptorControl);
        when(factory.newControl(eq(ContainmentAcceptorControl.class))).thenReturn(containmentAcceptorControl);
        when(factory.newControl(eq(DockingAcceptorControl.class))).thenReturn(dockingAcceptorControl);
        when(factory.newControl(eq(CanvasInPlaceTextEditorControl.class))).thenReturn(canvasInPlaceTextEditorControl);
        when(factory.newControl(eq(ToolboxControl.class))).thenReturn(toolboxControl);
        when(factory.newControl(eq(ElementBuilderControl.class))).thenReturn(builderControl);
        when(factory.newControl(eq(KeyboardControl.class))).thenReturn(keyboardControl);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        this.session = new CaseManagementClientFullSession(factory,
                                                           canvasCommandManager,
                                                           sessionCommandManager,
                                                           requestCommandManager,
                                                           registryFactory);
    }

    @Test
    public void testInit() {
        assertEquals(canvas,
                     session.getCanvas());
        assertEquals(canvasHandler,
                     session.getCanvasHandler());
        assertEquals(selectionControl,
                     session.getSelectionControl());
        assertEquals(zoomControl,
                     session.getZoomControl());
        assertEquals(panControl,
                     session.getPanControl());
        assertEquals(canvasCommandManager,
                     session.getCommandManager());
        assertEquals(connectionAcceptorControl,
                     session.getConnectionAcceptorControl());
        assertEquals(containmentAcceptorControl,
                     session.getContainmentAcceptorControl());
        assertEquals(dockingAcceptorControl,
                     session.getDockingAcceptorControl());
        assertEquals(builderControl,
                     session.getBuilderControl());
        assertEquals(canvasInPlaceTextEditorControl,
                     session.getCanvasInPlaceTextEditorControl());
    }

    @Test
    public void testOpenSession() {
        session.open();

        verify(canvas,
               times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).addRegistrationListener(any(CanvasElementListener.class));
        verify(selectionControl,
               times(1)).enable(eq(canvasHandler));
        verify(zoomControl,
               times(1)).enable(eq(canvas));
        verify(panControl,
               times(1)).enable(eq(canvas));
        verify(connectionAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(containmentAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(dockingAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(canvasInPlaceTextEditorControl,
               times(1)).enable(eq(canvasHandler));
        verify(builderControl,
               times(1)).enable(eq(canvasHandler));
    }

    @Test
    public void testDestroySession() {
        session.open();

        session.destroy();

        assertFalse(session.isOpened());
        verify(canvas,
               times(1)).removeRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).removeRegistrationListener(any(CanvasElementListener.class));
        verify(canvasHandler,
               times(1)).destroy();
        verify(selectionControl,
               times(1)).disable();
        verify(zoomControl,
               times(1)).disable();
        verify(panControl,
               times(1)).disable();
        verify(connectionAcceptorControl,
               times(1)).disable();
        verify(containmentAcceptorControl,
               times(1)).disable();
        verify(dockingAcceptorControl,
               times(1)).disable();
        verify(canvasInPlaceTextEditorControl,
               times(1)).disable();
        verify(builderControl,
               times(1)).disable();
    }
}
