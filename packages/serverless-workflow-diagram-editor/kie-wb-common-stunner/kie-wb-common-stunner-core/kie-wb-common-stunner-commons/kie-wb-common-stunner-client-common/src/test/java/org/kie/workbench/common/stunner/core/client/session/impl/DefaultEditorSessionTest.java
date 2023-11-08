/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.function.Consumer;

import jakarta.enterprise.event.Event;
import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.Registry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultEditorSessionTest {

    @Mock
    private ManagedSession managedSession;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Registry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;

    @Mock
    private DefaultRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> redoCommandRegistry;

    @Mock
    private Event<RegisterChangedEvent> registerChangedEvent;

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
                                          commandRegistry,
                                          redoCommandRegistry,
                                          registerChangedEvent);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstruct() {
        tested.constructInstance();
        verify(managedSession, times(1)).onCanvasControlRegistered(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasControlDestroyed(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasHandlerControlRegistered(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasHandlerControlDestroyed(any(Consumer.class));
        verify(commandRegistry).setRegistryChangeListener(any());
    }

    @Test
    public void testInit() {
        Metadata metadata = mock(Metadata.class);
        Command command = mock(Command.class);
        tested.init(metadata,
                    command);
        verify(managedSession, times(1)).registerCanvasControl(eq(MediatorsControl.class));
        verify(managedSession, times(1)).registerCanvasControl(eq(AlertsControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(SelectionControl.class),
                                                                      eq(MultipleSelection.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ResizeControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ConnectionAcceptorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(ContainmentAcceptorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(DockingAcceptorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(CanvasInlineTextEditorControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(LineSpliceAcceptorControl.class));
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
    public void testClose() {
        tested.close();
        verify(commandRegistry, never()).clear();
        verify(managedSession, times(1)).close();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(commandRegistry, times(1)).clear();
        verify(managedSession, times(1)).destroy();
    }
}
