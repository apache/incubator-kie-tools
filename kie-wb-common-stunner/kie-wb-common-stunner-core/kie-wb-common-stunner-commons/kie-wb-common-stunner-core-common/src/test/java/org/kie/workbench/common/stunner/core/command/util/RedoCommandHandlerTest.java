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
package org.kie.workbench.common.stunner.core.command.util;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.registry.impl.ClientCommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedoCommandHandlerTest {

    @Mock
    private Command command1;

    @Mock
    private Command command2;

    @Mock
    private ClientCommandRegistry clientCommandRegistry;

    @Mock
    private Event<RegisterChangedEvent> registerChangedEvent;

    private RedoCommandHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = new RedoCommandHandler(clientCommandRegistry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoCommandExecuted() {
        ClientCommandRegistry realRegistry = spy(new ClientCommandRegistry(registerChangedEvent));
        RedoCommandHandler tested = new RedoCommandHandler(realRegistry);
        assertTrue(tested.onUndoCommandExecuted(command1));
        verify(realRegistry).register(eq(command1));
        assertTrue(tested.isEnabled());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        Object obj = mock(Object.class);
        CommandManager manager = mock(CommandManager.class);
        CommandResult expectedResult = mock(CommandResult.class);
        when(clientCommandRegistry.peek()).thenReturn(command1);
        when(manager.execute(obj,
                             command1)).thenReturn(expectedResult);

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
        when(clientCommandRegistry.isEmpty()).thenReturn(true);

        RedoCommandHandler tested = new RedoCommandHandler(clientCommandRegistry);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     tested.execute(obj, manager));
        verify(manager, never()).execute(anyObject(), any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteJustRecentRedoCommand() {
        RedoCommandHandler tested = new RedoCommandHandler(spy(new ClientCommandRegistry(registerChangedEvent)));
        assertTrue(tested.onUndoCommandExecuted(command1));
        assertFalse(tested.onCommandExecuted(command1));
        assertFalse(tested.isEnabled());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteRemoveRedoCommands() {
        RedoCommandHandler tested = new RedoCommandHandler(spy(new ClientCommandRegistry(registerChangedEvent)));
        Command command3 = mock(Command.class);
        assertTrue(tested.onUndoCommandExecuted(command1));
        assertTrue(tested.onUndoCommandExecuted(command2));
        assertFalse(tested.onCommandExecuted(command3));
        assertFalse(tested.isEnabled());
    }
}
