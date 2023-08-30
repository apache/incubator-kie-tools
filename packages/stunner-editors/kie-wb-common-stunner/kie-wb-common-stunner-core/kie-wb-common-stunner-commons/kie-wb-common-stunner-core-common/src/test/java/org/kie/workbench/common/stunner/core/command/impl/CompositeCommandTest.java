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


package org.kie.workbench.common.stunner.core.command.impl;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompositeCommandTest {

    private static CommandResult<RuleViolation> SOME_ERROR_VIOLATION =
            new CommandResultImpl<>(CommandResult.Type.ERROR,
                                    Collections.singletonList(new RuleViolationImpl("failed")));

    @Mock
    GraphCommandExecutionContext commandExecutionContext;
    private CompositeCommandStub tested;

    @Before
    public void setup() throws Exception {
        this.tested = spy(new CompositeCommandStub());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitialize() {
        assertFalse(tested.isInitialized());
        tested.ensureInitialized(commandExecutionContext);
        assertTrue(tested.isInitialized());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(commandExecutionContext);
        assertNotNull(result);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(tested,
               times(1)).addCommand(any(CommandStub.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(commandExecutionContext);
        assertNotNull(result);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(tested,
               times(1)).addCommand(any(CommandStub.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute1Failed() {
        Command c1 = mockSuccessCommandOperations();
        when(c1.execute(eq(commandExecutionContext))).thenReturn(SOME_ERROR_VIOLATION);
        Command c2 = mockSuccessCommandOperations();
        Command c3 = mockSuccessCommandOperations();
        CompositeCommand command = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = command.execute(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c1, times(1)).execute(eq(commandExecutionContext));
        verify(c1, never()).undo(eq(commandExecutionContext));
        verify(c2, never()).execute(eq(commandExecutionContext));
        verify(c2, never()).undo(eq(commandExecutionContext));
        verify(c3, never()).execute(eq(commandExecutionContext));
        verify(c3, never()).undo(eq(commandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute2FailedThenUndo() {
        Command c1 = mockSuccessCommandOperations();
        Command c2 = mockSuccessCommandOperations();
        when(c2.execute(eq(commandExecutionContext))).thenReturn(SOME_ERROR_VIOLATION);
        Command c3 = mockSuccessCommandOperations();
        CompositeCommand command = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = command.execute(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c1, times(1)).execute(eq(commandExecutionContext));
        verify(c1, times(1)).undo(eq(commandExecutionContext));
        verify(c2, times(1)).execute(eq(commandExecutionContext));
        verify(c2, never()).undo(eq(commandExecutionContext));
        verify(c3, never()).execute(eq(commandExecutionContext));
        verify(c3, never()).undo(eq(commandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute3FailedThenUndo() {
        Command c1 = mockSuccessCommandOperations();
        Command c2 = mockSuccessCommandOperations();
        Command c3 = mockSuccessCommandOperations();
        when(c3.execute(eq(commandExecutionContext))).thenReturn(SOME_ERROR_VIOLATION);
        CompositeCommand command = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = command.execute(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c1, times(1)).execute(eq(commandExecutionContext));
        verify(c1, times(1)).undo(eq(commandExecutionContext));
        verify(c2, times(1)).execute(eq(commandExecutionContext));
        verify(c2, times(1)).undo(eq(commandExecutionContext));
        verify(c3, times(1)).execute(eq(commandExecutionContext));
        verify(c3, never()).undo(eq(commandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo3Failed() {
        Command c1 = mockSuccessCommandOperations();
        Command c2 = mockSuccessCommandOperations();
        Command c3 = mockSuccessCommandOperations();
        when(c3.undo(eq(commandExecutionContext))).thenReturn(SOME_ERROR_VIOLATION);
        CompositeCommand command = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = command.undo(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c3, times(1)).undo(eq(commandExecutionContext));
        verify(c3, never()).execute(eq(commandExecutionContext));
        verify(c2, never()).undo(eq(commandExecutionContext));
        verify(c2, never()).execute(eq(commandExecutionContext));
        verify(c1, never()).undo(eq(commandExecutionContext));
        verify(c1, never()).execute(eq(commandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo2FailedSoRedo() {
        Command c1 = mockSuccessCommandOperations();
        Command c2 = mockSuccessCommandOperations();
        when(c2.undo(eq(commandExecutionContext))).thenReturn(SOME_ERROR_VIOLATION);
        Command c3 = mockSuccessCommandOperations();
        CompositeCommand command = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = command.undo(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c3, times(1)).undo(eq(commandExecutionContext));
        verify(c3, times(1)).execute(eq(commandExecutionContext));
        verify(c2, times(1)).undo(eq(commandExecutionContext));
        verify(c2, never()).execute(eq(commandExecutionContext));
        verify(c1, never()).undo(eq(commandExecutionContext));
        verify(c1, never()).execute(eq(commandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo1FailedSoRedo() {
        Command c1 = mockSuccessCommandOperations();
        when(c1.undo(eq(commandExecutionContext))).thenReturn(SOME_ERROR_VIOLATION);
        Command c2 = mockSuccessCommandOperations();
        Command c3 = mockSuccessCommandOperations();
        CompositeCommand command = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = command.undo(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c3, times(1)).undo(eq(commandExecutionContext));
        verify(c3, times(1)).execute(eq(commandExecutionContext));
        verify(c2, times(1)).undo(eq(commandExecutionContext));
        verify(c2, times(1)).execute(eq(commandExecutionContext));
        verify(c1, times(1)).undo(eq(commandExecutionContext));
        verify(c1, never()).execute(eq(commandExecutionContext));
    }

    @SuppressWarnings("unchecked")
    private Command mockSuccessCommandOperations() {
        Command command = mock(Command.class);
        when(command.allow(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        when(command.execute(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        when(command.undo(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        return command;
    }

    private static class CompositeCommandStub extends AbstractCompositeCommand<GraphCommandExecutionContext, RuleViolation> {

        @Override
        protected CompositeCommandStub initialize(GraphCommandExecutionContext context) {
            super.initialize(context);
            addCommand(new CommandStub());
            return this;
        }

        @Override
        protected CommandResult<RuleViolation> doAllow(GraphCommandExecutionContext context,
                                                       Command<GraphCommandExecutionContext, RuleViolation> command) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        @Override
        protected CommandResult<RuleViolation> doExecute(GraphCommandExecutionContext context,
                                                         Command<GraphCommandExecutionContext, RuleViolation> command) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        @Override
        protected CommandResult<RuleViolation> doUndo(GraphCommandExecutionContext context,
                                                      Command<GraphCommandExecutionContext, RuleViolation> command) {
            return GraphCommandResultBuilder.SUCCESS;
        }
    }

    private static class CommandStub extends AbstractGraphCommand {

        private final CommandResult<RuleViolation> executeResult;

        private CommandStub() {
            this.executeResult = GraphCommandResultBuilder.SUCCESS;
        }

        public CommandStub(CommandResult<RuleViolation> executeResult) {
            this.executeResult = executeResult;
        }

        @Override
        protected CommandResult<RuleViolation> check(GraphCommandExecutionContext context) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        @Override
        public CommandResult<RuleViolation> allow(GraphCommandExecutionContext context) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        @Override
        public CommandResult<RuleViolation> execute(GraphCommandExecutionContext context) {
            return executeResult;
        }

        @Override
        public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
            return GraphCommandResultBuilder.SUCCESS;
        }
    }
}
