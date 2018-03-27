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

package org.kie.workbench.common.stunner.core.client.session.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
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
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ClientFullSessionTest {

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
    private LocationControl<AbstractCanvasHandler, Element> locationControl;

    @Mock
    private ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;

    @Mock
    private ElementBuilderControl<AbstractCanvasHandler> builderControl;

    @Mock
    private KeyboardControl<Canvas, ClientSession> keyboardControl;

    @Mock
    private ControlPointControl controlPointControl;

    @Mock
    private Disposer disposer;

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
        when(factory.newControl(eq(LocationControl.class))).thenReturn(locationControl);
        when(factory.newControl(eq(CanvasInPlaceTextEditorControl.class))).thenReturn(canvasInPlaceTextEditorControl);
        when(factory.newControl(eq(ToolboxControl.class))).thenReturn(toolboxControl);
        when(factory.newControl(eq(ElementBuilderControl.class))).thenReturn(builderControl);
        when(factory.newControl(eq(KeyboardControl.class))).thenReturn(keyboardControl);
        when(factory.newControl(eq(ControlPointControl.class))).thenReturn(controlPointControl);
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
        assertEquals(canvasInPlaceTextEditorControl,
                     tested.getCanvasInPlaceTextEditorControl());
        assertEquals(locationControl,
                     tested.getLocationControl());
        assertEquals(toolboxControl,
                     tested.getToolboxControl());
        assertEquals(builderControl,
                     tested.getBuilderControl());
        assertEquals(keyboardControl,
                     tested.getKeyboardControl());
        assertEquals(controlPointControl,
                     tested.getControlPointControl());

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
        verify(locationControl,
               times(1)).setCommandManagerProvider(drag.capture());
        assertEquals(requestCommandManager,
                     drag.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> resize =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(resizeControl,
               times(1)).setCommandManagerProvider(resize.capture());
        assertEquals(sessionCommandManager,
                     resize.getValue().getCommandManager());
        final ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> name =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(canvasInPlaceTextEditorControl,
               times(1)).setCommandManagerProvider(name.capture());
        assertEquals(sessionCommandManager,
                     name.getValue().getCommandManager());
    }

    @Test
    @SuppressWarnings("unchecked")
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
        verify(canvasInPlaceTextEditorControl,
               times(1)).enable(eq(canvasHandler));
        verify(locationControl,
               times(1)).enable(eq(canvasHandler));
        verify(toolboxControl,
               times(1)).enable(eq(canvasHandler));
        verify(builderControl,
               times(1)).enable(eq(canvasHandler));
        verify(keyboardControl,
               times(1)).bind(eq(tested));
        verify(controlPointControl,
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
        verify(canvasInPlaceTextEditorControl,
               times(1)).disable();
        verify(locationControl,
               times(1)).disable();
        verify(toolboxControl,
               times(1)).disable();
        verify(builderControl,
               times(1)).disable();
        verify(keyboardControl,
               times(1)).disable();
        verify(controlPointControl,
               times(1)).disable();
    }

    @SuppressWarnings("unchecked")
    private void buildTestedInstance() {
        this.tested = new ClientFullSessionImpl(factory,
                                                canvasCommandManager,
                                                sessionCommandManager,
                                                requestCommandManager,
                                                registryFactory,
                                                disposer);
    }
}
