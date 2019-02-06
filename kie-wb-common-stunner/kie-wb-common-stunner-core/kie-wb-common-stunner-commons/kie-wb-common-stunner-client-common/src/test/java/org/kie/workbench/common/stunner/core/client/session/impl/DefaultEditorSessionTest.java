/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.MultiLineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.impl.ClientCommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEditorSessionTest {

    @Mock
    private ManagedSession managedSession;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> requestCommandManage;

    @Mock
    private ClientCommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> clientCommandRegistry;

    private DefaultEditorSession tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(managedSession.onCanvasControlRegistered(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.onCanvasControlDestroyed(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.onCanvasHandlerControlRegistered(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.onCanvasHandlerControlDestroyed(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.registerCanvasControl(any(Class.class))).thenReturn(managedSession);
        when(managedSession.registerCanvasControl(any(Class.class),
                                                  any(Class.class))).thenReturn(managedSession);
        when(managedSession.registerCanvasHandlerControl(any(Class.class))).thenReturn(managedSession);
        when(managedSession.registerCanvasHandlerControl(any(Class.class),
                                                         any(Class.class))).thenReturn(managedSession);
        tested = new DefaultEditorSession(managedSession,
                                          canvasCommandManager,
                                          sessionCommandManager,
                                          requestCommandManage,
                                          clientCommandRegistry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstruct() {
        tested.constructInstance();
        verify(managedSession, times(1)).onCanvasControlRegistered(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasControlDestroyed(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasHandlerControlRegistered(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasHandlerControlDestroyed(any(Consumer.class));
    }

    @Test
    public void testInit() {
        Metadata metadata = mock(Metadata.class);
        Command command = mock(Command.class);
        tested.init(metadata,
                    command);
        verify(managedSession, times(1)).registerCanvasControl(eq(ZoomControl.class));
        verify(managedSession, times(1)).registerCanvasControl(eq(PanControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(SelectionControl.class),
                                                                      eq(MultipleSelection.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ResizeControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ConnectionAcceptorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ContainmentAcceptorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(DockingAcceptorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(CanvasInPlaceTextEditorControl.class),
                                                                      eq(MultiLineTextEditorBox.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(LocationControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ToolboxControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ElementBuilderControl.class),
                                                                      eq(Observer.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(NodeBuilderControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(EdgeBuilderControl.class));
        verify(managedSession, times(1)).registerCanvasControl(eq(KeyboardControl.class));
        verify(managedSession, times(1)).registerCanvasControl(eq(ClipboardControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ControlPointControl.class));
        verify(managedSession, times(1)).init(eq(metadata),
                                              eq(command));
    }

    @Test
    public void testOpen() {
        tested.open();
        verify(managedSession, times(1)).open();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(clientCommandRegistry, times(1)).clear();
        verify(managedSession, times(1)).destroy();
    }
}
