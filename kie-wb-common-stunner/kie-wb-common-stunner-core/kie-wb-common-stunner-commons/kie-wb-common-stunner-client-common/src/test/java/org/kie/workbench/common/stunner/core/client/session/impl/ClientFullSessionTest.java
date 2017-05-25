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

package org.kie.workbench.common.stunner.core.client.session.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasNameEditionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ClientFullSessionTest {

    @Mock
    CanvasFactory<AbstractCanvas, AbstractCanvasHandler> factory;
    @Mock
    AbstractCanvas canvas;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    @Mock
    ZoomControl<AbstractCanvas> zoomControl;
    @Mock
    PanControl<AbstractCanvas> panControl;
    @Mock
    ResizeControl<AbstractCanvasHandler, Element> resizeControl;
    @Mock
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    @Mock
    SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    @Mock
    SessionCommandManager<AbstractCanvasHandler> requestCommandManager;
    @Mock
    RegistryFactory registryFactory;
    @Mock
    ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    @Mock
    ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    @Mock
    DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    @Mock
    CanvasNameEditionControl<AbstractCanvasHandler, Element> canvasNameEditionControl;
    @Mock
    DragControl<AbstractCanvasHandler, Element> dragControl;
    @Mock
    ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;
    @Mock
    ElementBuilderControl<AbstractCanvasHandler> builderControl;

    private ClientFullSessionImpl tested;

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
        when(factory.newControl(eq(DragControl.class))).thenReturn(dragControl);
        when(factory.newControl(eq(CanvasNameEditionControl.class))).thenReturn(canvasNameEditionControl);
        when(factory.newControl(eq(ToolboxControl.class))).thenReturn(toolboxControl);
        when(factory.newControl(eq(ElementBuilderControl.class))).thenReturn(builderControl);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        buildTestedInstance();
        // Assert session's public getters.
        assertEquals(canvas,
                     tested.getCanvas());
        assertEquals(canvasHandler,
                     tested.getCanvasHandler());
        assertEquals(selectionControl,
                     tested.getSelectionControl());
        assertEquals(zoomControl,
                     tested.getZoomControl());
        assertEquals(panControl,
                     tested.getPanControl());
        assertEquals(resizeControl,
                     tested.getResizeControl());
        assertEquals(canvasCommandManager,
                     tested.getCommandManager());
        assertEquals(connectionAcceptorControl,
                     tested.getConnectionAcceptorControl());
        assertEquals(containmentAcceptorControl,
                     tested.getContainmentAcceptorControl());
        assertEquals(dockingAcceptorControl,
                     tested.getDockingAcceptorControl());
        assertEquals(canvasNameEditionControl,
                     tested.getCanvasNameEditionControl());
        assertEquals(dragControl,
                     tested.getDragControl());
        assertEquals(toolboxControl,
                     tested.getToolboxControl());
        assertEquals(builderControl,
                     tested.getBuilderControl());
        // Assert setting the right command manager for each control.
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> conn =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(connectionAcceptorControl,
               times(1)).setCommandManagerProvider(conn.capture());
        assertEquals(requestCommandManager,
                     conn.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> cont =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(containmentAcceptorControl,
               times(1)).setCommandManagerProvider(cont.capture());
        assertEquals(requestCommandManager,
                     cont.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> docking =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(dockingAcceptorControl,
               times(1)).setCommandManagerProvider(docking.capture());
        assertEquals(requestCommandManager,
                     docking.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> builder =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(builderControl,
               times(1)).setCommandManagerProvider(builder.capture());
        assertEquals(sessionCommandManager,
                     builder.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> drag =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(dragControl,
               times(1)).setCommandManagerProvider(drag.capture());
        assertEquals(requestCommandManager,
                     drag.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> resize =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(resizeControl,
               times(1)).setCommandManagerProvider(resize.capture());
        assertEquals(sessionCommandManager,
                     resize.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> toolbox =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(toolboxControl,
               times(1)).setCommandManagerProvider(toolbox.capture());
        assertEquals(sessionCommandManager,
                     toolbox.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> name =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(canvasNameEditionControl,
               times(1)).setCommandManagerProvider(name.capture());
        assertEquals(sessionCommandManager,
                     name.getValue().getCommandManager());
    }

    @Test
    public void testOpenSession() {
        buildTestedInstance();
        tested.open();
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
        verify(resizeControl,
               times(1)).enable(eq(canvasHandler));
        verify(connectionAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(containmentAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(dockingAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(canvasNameEditionControl,
               times(1)).enable(eq(canvasHandler));
        verify(dragControl,
               times(1)).enable(eq(canvasHandler));
        verify(toolboxControl,
               times(1)).enable(eq(canvasHandler));
        verify(builderControl,
               times(1)).enable(eq(canvasHandler));
    }

    @Test
    public void testDestroySession() {
        buildTestedInstance();
        tested.isOpened = true;
        tested.doOpen(); // Force to register listeners.
        tested.destroy();
        assertFalse(tested.isOpened());
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
        verify(resizeControl,
               times(1)).disable();
        verify(connectionAcceptorControl,
               times(1)).disable();
        verify(containmentAcceptorControl,
               times(1)).disable();
        verify(dockingAcceptorControl,
               times(1)).disable();
        verify(canvasNameEditionControl,
               times(1)).disable();
        verify(dragControl,
               times(1)).disable();
        verify(toolboxControl,
               times(1)).disable();
        verify(builderControl,
               times(1)).disable();
    }

    private void buildTestedInstance() {
        this.tested = new ClientFullSessionImpl(factory,
                                                canvasCommandManager,
                                                sessionCommandManager,
                                                requestCommandManager,
                                                registryFactory);
    }
}
