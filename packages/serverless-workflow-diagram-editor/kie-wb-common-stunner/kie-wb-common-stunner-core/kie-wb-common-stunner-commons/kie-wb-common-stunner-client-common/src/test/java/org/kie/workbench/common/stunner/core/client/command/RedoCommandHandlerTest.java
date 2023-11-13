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

import jakarta.enterprise.event.Event;
import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedoCommandHandlerTest {

    @Mock
    private Command command1;

    @Mock
    private Command command2;

    @Mock
    private Event<RegisterChangedEvent> registerChangedEvent;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private EditorSession session;

    private DefaultRegistry commandRegistry;

    private RedoCommandHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        commandRegistry = spy(new DefaultRegistryImpl<>());
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getRedoCommandRegistry()).thenReturn(commandRegistry);
        this.tested = new RedoCommandHandler(sessionManager, registerChangedEvent);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoCommandExecuted() {
        assertTrue(tested.onUndoCommandExecuted(command1));
        verify(commandRegistry).register(eq(command1));
        assertTrue(tested.isEnabled());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        Object obj = mock(Object.class);
        CommandManager manager = mock(CommandManager.class);
        CommandResult expectedResult = mock(CommandResult.class);
        when(commandRegistry.isEmpty()).thenReturn(false);
        when(commandRegistry.peek()).thenReturn(command1);
        when(manager.execute(anyObject(),
                             eq(command1))).thenReturn(expectedResult);

        CommandResult actualResult = tested.execute(obj,
                                                    manager);
        assertEquals(expectedResult,
                     actualResult);
        verify(manager).execute(eq(obj),
                                eq(command1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteOnNull() {
        Object obj = mock(Object.class);
        CommandManager manager = mock(CommandManager.class);
        when(commandRegistry.isEmpty()).thenReturn(true);

        RedoCommandHandler tested = new RedoCommandHandler(sessionManager, registerChangedEvent);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     tested.execute(obj, manager));
        verify(manager, never()).execute(any(), any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteJustRecentRedoCommand() {
        assertTrue(tested.onUndoCommandExecuted(command1));
        assertFalse(tested.onCommandExecuted(command1));
        assertFalse(tested.isEnabled());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteRemoveRedoCommands() {
        Command command3 = mock(Command.class);
        assertTrue(tested.onUndoCommandExecuted(command1));
        assertTrue(tested.onUndoCommandExecuted(command2));
        assertFalse(tested.onCommandExecuted(command3));
        assertFalse(tested.isEnabled());
    }

    @Test
    public void testSetSession() {
        ClientSession session = mock(ClientSession.class);
        tested.setSession(session);
        verify(commandRegistry, times(1)).setRegistryChangeListener(any());
    }
}
