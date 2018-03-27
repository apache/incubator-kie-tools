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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasCommandHasGraphTest extends AbstractCanvasCommandTest {

    @Mock
    GraphCommandExecutionContext graphCommandExecutionContext;
    @Mock
    Command<GraphCommandExecutionContext, RuleViolation> graphCommand;
    @Mock
    AbstractCanvasCommand canvasCommand;

    @Mock
    CommandResult<RuleViolation> successGraphCommandResult;
    @Mock
    CommandResult<RuleViolation> failedGraphCommandResult;

    private HasGraphCommandStub tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphCommandExecutionContext);
        when(successGraphCommandResult.getType()).thenReturn(CommandResult.Type.INFO);
        when(successGraphCommandResult.getViolations()).thenReturn(null);
        when(failedGraphCommandResult.getType()).thenReturn(CommandResult.Type.ERROR);
        this.tested = new HasGraphCommandStub();
    }

    @Test
    public void testAllowSuccess() {
        when(graphCommand.allow(any(GraphCommandExecutionContext.class))).thenReturn(successGraphCommandResult);
        tested.allow(canvasHandler);
        verify(graphCommand,
               times(1)).allow(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(1)).allow(eq(canvasHandler));
        verify(graphCommand,
               times(0)).execute(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).execute(eq(canvasHandler));
    }

    @Test
    public void testAllowFailed() {
        when(graphCommand.allow(any(GraphCommandExecutionContext.class))).thenReturn(failedGraphCommandResult);
        tested.allow(canvasHandler);
        verify(graphCommand,
               times(1)).allow(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).allow(eq(canvasHandler));
        verify(graphCommand,
               times(0)).execute(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).execute(eq(canvasHandler));
    }

    @Test
    public void testExecuteSuccess() {
        when(graphCommand.allow(any(GraphCommandExecutionContext.class))).thenReturn(successGraphCommandResult);
        when(graphCommand.execute(any(GraphCommandExecutionContext.class))).thenReturn(successGraphCommandResult);
        tested.execute(canvasHandler);
        verify(graphCommand,
               times(1)).execute(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(1)).execute(eq(canvasHandler));
        verify(graphCommand,
               times(0)).allow(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).allow(eq(canvasHandler));
    }

    @Test
    public void testExecuteFailed() {
        when(graphCommand.allow(any(GraphCommandExecutionContext.class))).thenReturn(failedGraphCommandResult);
        when(graphCommand.execute(any(GraphCommandExecutionContext.class))).thenReturn(failedGraphCommandResult);
        tested.execute(canvasHandler);
        verify(graphCommand,
               times(1)).execute(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).execute(eq(canvasHandler));
        verify(graphCommand,
               times(0)).allow(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).allow(eq(canvasHandler));
    }

    @Test
    public void testAllowNoGraphContext() {
        when(canvasHandler.getGraphExecutionContext()).thenReturn(null);
        tested.allow(canvasHandler);
        verify(graphCommand,
               times(0)).allow(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(1)).allow(eq(canvasHandler));
        verify(graphCommand,
               times(0)).execute(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).execute(eq(canvasHandler));
    }

    @Test
    public void testExecuteNoGraphContext() {
        when(canvasHandler.getGraphExecutionContext()).thenReturn(null);
        tested.execute(canvasHandler);
        verify(graphCommand,
               times(0)).execute(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(1)).execute(eq(canvasHandler));
        verify(graphCommand,
               times(0)).allow(any(GraphCommandExecutionContext.class));
        verify(canvasCommand,
               times(0)).allow(eq(canvasHandler));
    }

    private class HasGraphCommandStub extends AbstractCanvasGraphCommand {

        @Override
        protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(AbstractCanvasHandler context) {
            return graphCommand;
        }

        @Override
        protected AbstractCanvasCommand newCanvasCommand(AbstractCanvasHandler context) {
            return canvasCommand;
        }
    }
}
