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
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompositeCommandTest {

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
    public void testExecuteFailedThenUndo() {
        Command c1 = mock(Command.class);
        when(c1.allow(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        when(c1.execute(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        Command c2 = mock(Command.class);
        when(c2.allow(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        when(c2.execute(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        CommandResult<RuleViolation> failed =
                new CommandResultImpl<>(CommandResult.Type.ERROR,
                                        Collections.singletonList(new RuleViolationImpl("failed")));
        Command c3 = mock(Command.class);
        when(c3.allow(eq(commandExecutionContext))).thenReturn(GraphCommandResultBuilder.SUCCESS);
        when(c3.execute(eq(commandExecutionContext))).thenReturn(failed);
        CompositeCommand composite = new CompositeCommand.Builder<>()
                .addCommand(c1)
                .addCommand(c2)
                .addCommand(c3)
                .build();
        CommandResult result = composite.execute(commandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(c1, times(1)).undo(eq(commandExecutionContext));
        verify(c2, times(1)).undo(eq(commandExecutionContext));
        verify(c3, times(1)).undo(eq(commandExecutionContext));
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
