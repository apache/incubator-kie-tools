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

package org.kie.workbench.common.stunner.core.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandRegistryListenerTest {

    private class CommandRegistryListenerStub extends CommandRegistryListener {

        @Override
        protected CommandRegistry<Command> getRegistry() {
            return commandRegistry;
        }

        @Override
        public void onAllow(Object context,
                            Command command,
                            CommandResult result) {
        }
    }

    @Mock
    CommandRegistry<Command> commandRegistry;
    private CommandRegistryListenerStub tested;

    @Before
    public void setup() throws Exception {
        tested = new CommandRegistryListenerStub();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccess() {
        Object context = mock(Object.class);
        Command command = mock(Command.class);
        CommandResult result = mock(CommandResult.class);
        when(result.getType()).thenReturn(CommandResult.Type.INFO);
        tested.onExecute(context,
                         command,
                         result);
        verify(commandRegistry,
               times(1)).register(eq(command));
        verify(commandRegistry,
               times(0)).pop();
        verify(commandRegistry,
               times(0)).peek();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteFailed() {
        Object context = mock(Object.class);
        Command command = mock(Command.class);
        CommandResult result = mock(CommandResult.class);
        when(result.getType()).thenReturn(CommandResult.Type.ERROR);
        tested.onExecute(context,
                         command,
                         result);
        verify(commandRegistry,
               times(0)).register(eq(command));
        verify(commandRegistry,
               times(0)).pop();
        verify(commandRegistry,
               times(0)).peek();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoSuccess() {
        Object context = mock(Object.class);
        Command command = mock(Command.class);
        CommandResult result = mock(CommandResult.class);
        when(result.getType()).thenReturn(CommandResult.Type.INFO);
        tested.onUndo(context,
                      command,
                      result);
        verify(commandRegistry,
               times(1)).pop();
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).register(any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoFailed() {
        Object context = mock(Object.class);
        Command command = mock(Command.class);
        CommandResult result = mock(CommandResult.class);
        when(result.getType()).thenReturn(CommandResult.Type.ERROR);
        tested.onUndo(context,
                      command,
                      result);
        verify(commandRegistry,
               times(0)).pop();
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).register(any(Command.class));
    }
}
