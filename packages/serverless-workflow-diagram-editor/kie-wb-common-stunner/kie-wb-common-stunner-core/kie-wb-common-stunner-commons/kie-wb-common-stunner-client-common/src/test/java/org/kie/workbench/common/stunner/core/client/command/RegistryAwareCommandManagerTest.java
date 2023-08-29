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


package org.kie.workbench.common.stunner.core.client.command;

import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistryAwareCommandManagerTest {

    private final Command<AbstractCanvasHandler, CanvasViolation> COMMAND_SUCCESS1 = buildCommand(true);
    private final Command<AbstractCanvasHandler, CanvasViolation> COMMAND_SUCCESS2 = buildCommand(true);
    private final Command<AbstractCanvasHandler, CanvasViolation> COMMAND_FAILED = buildCommand(false);

    @Mock
    private EditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private SessionManager sessionManager;
    private RegistryAwareCommandManager tested;
    private Registry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Before
    public void setUp() {
        commandManager = spy(new CanvasCommandManagerStub());
        commandRegistry = spy(new DefaultRegistryImpl<>());
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getCommandManager()).thenReturn(commandManager);
        when(session.getCommandRegistry()).thenReturn(commandRegistry);
        tested = new RegistryAwareCommandManager(sessionManager);
        when(sessionManager.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testAllow() {
        tested.allow(COMMAND_SUCCESS1);
        verify(commandManager, times(1)).allow(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, never()).execute(any(), any());
        verify(commandManager, never()).undo(any(), any());
        verify(commandRegistry, never()).register(any());
        verify(commandRegistry, never()).pop();
        verify(commandRegistry, never()).peek();
        verify(commandRegistry, never()).clear();
    }

    @Test
    public void testExecute() {
        tested.execute(COMMAND_SUCCESS1);
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, never()).allow(any(), any());
        verify(commandManager, never()).undo(any(), any());
        verify(commandRegistry, times(1)).register(any());
        assertEquals(1, commandRegistry.getHistory().size());
        assertEquals(COMMAND_SUCCESS1, commandRegistry.getHistory().get(0));
        verify(commandRegistry, never()).pop();
        verify(commandRegistry, never()).peek();
        verify(commandRegistry, never()).clear();
    }

    @Test
    public void testUndo() {
        commandRegistry.register(COMMAND_SUCCESS1);
        tested.undo();
        verify(commandManager, times(1)).undo(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, never()).execute(any(), any());
        verify(commandManager, never()).allow(any(), any());
        verify(commandRegistry, times(1)).pop();
    }

    @Test
    public void testSuccessfulRequest() {
        tested.start();
        tested.allow(COMMAND_SUCCESS1);
        tested.allow(COMMAND_SUCCESS2);
        tested.execute(COMMAND_SUCCESS1);
        tested.execute(COMMAND_SUCCESS2);
        tested.complete();
        verify(commandManager, times(1)).allow(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, times(1)).allow(eq(canvasHandler), eq(COMMAND_SUCCESS2));
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(COMMAND_SUCCESS2));
        verify(commandRegistry, times(1)).register(any());
        assertEquals(1, commandRegistry.getHistory().size());
        CompositeCommand<AbstractCanvasHandler, CanvasViolation> command =
                (CompositeCommand<AbstractCanvasHandler, CanvasViolation>) commandRegistry.getHistory().get(0);
        assertEquals(2, command.getCommands().size());
        assertEquals(COMMAND_SUCCESS1, command.getCommands().get(0));
        assertEquals(COMMAND_SUCCESS2, command.getCommands().get(1));
        verify(commandRegistry, never()).pop();
        verify(commandRegistry, never()).peek();
        verify(commandRegistry, never()).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFailedRequest() {
        tested.start();
        tested.allow(COMMAND_SUCCESS1);
        tested.allow(COMMAND_SUCCESS2);
        tested.execute(COMMAND_SUCCESS1);
        tested.execute(COMMAND_SUCCESS2);
        tested.execute(COMMAND_FAILED);
        tested.complete();
        verify(commandManager, times(1)).allow(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, times(1)).allow(eq(canvasHandler), eq(COMMAND_SUCCESS2));
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(COMMAND_SUCCESS1));
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(COMMAND_SUCCESS2));
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(COMMAND_FAILED));
        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandManager, times(1)).undo(eq(canvasHandler), commandCaptor.capture());
        CompositeCommand<AbstractCanvasHandler, CanvasViolation> command =
                (CompositeCommand<AbstractCanvasHandler, CanvasViolation>) commandCaptor.getValue();
        assertEquals(2, command.getCommands().size());
        assertEquals(COMMAND_SUCCESS1, command.getCommands().get(0));
        assertEquals(COMMAND_SUCCESS2, command.getCommands().get(1));
        verify(commandRegistry, never()).register(any());
        verify(commandRegistry, never()).pop();
        verify(commandRegistry, never()).peek();
        verify(commandRegistry, never()).clear();
    }

    private static Command<AbstractCanvasHandler, CanvasViolation> buildCommand(boolean success) {
        final CommandResult<CanvasViolation> result = success ?
                CanvasCommandResultBuilder.SUCCESS : CanvasCommandResultBuilder.failed();
        return new Command<AbstractCanvasHandler, CanvasViolation>() {
            @Override
            public CommandResult<CanvasViolation> allow(AbstractCanvasHandler context) {
                return result;
            }

            @Override
            public CommandResult<CanvasViolation> execute(AbstractCanvasHandler context) {
                return result;
            }

            @Override
            public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context) {
                return result;
            }
        };
    }
}
