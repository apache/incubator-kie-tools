/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCanvasGraphCommandTest {

    private static CommandResult<RuleViolation> GRAPH_COMMAND_SUCCESS = GraphCommandResultBuilder.SUCCESS;
    private static CommandResult<RuleViolation> GRAPH_COMMAND_FAILED = GraphCommandResultBuilder.failed();
    private static CommandResult<CanvasViolation> CANVAS_COMMAND_SUCCESS = CanvasCommandResultBuilder.SUCCESS;
    private static CommandResult<CanvasViolation> CANVAS_COMMAND_FAILED = CanvasCommandResultBuilder.failed();

    @Mock
    private Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    @Mock
    private Command<AbstractCanvasHandler, CanvasViolation> canvasCommand;

    @Mock
    private GraphCommandExecutionContext graphContext;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    private AbstractCanvasGraphCommand tested;

    @Before
    public void setUp() throws Exception {
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphContext);
        when(graphCommand.allow(eq(graphContext))).thenReturn(GRAPH_COMMAND_SUCCESS);
        when(graphCommand.execute(eq(graphContext))).thenReturn(GRAPH_COMMAND_SUCCESS);
        when(graphCommand.undo(eq(graphContext))).thenReturn(GRAPH_COMMAND_SUCCESS);
        when(canvasCommand.allow(eq(canvasHandler))).thenReturn(CANVAS_COMMAND_SUCCESS);
        when(canvasCommand.execute(eq(canvasHandler))).thenReturn(CANVAS_COMMAND_SUCCESS);
        when(canvasCommand.undo(eq(canvasHandler))).thenReturn(CANVAS_COMMAND_SUCCESS);
        tested = new AbstractCanvasGraphCommandStub();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        verify(graphCommand, times(1)).allow(eq(graphContext));
        verify(canvasCommand, times(1)).allow(eq(canvasHandler));
        assertEquals(CommandResult.Type.INFO, result.getType());
        assertFalse(result.getViolations().iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCanvasCommandNotAllowed() {
        when(canvasCommand.allow(eq(canvasHandler))).thenReturn(CANVAS_COMMAND_FAILED);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        verify(graphCommand, times(1)).allow(eq(graphContext));
        verify(canvasCommand, times(1)).allow(eq(canvasHandler));
        assertEquals(CommandResult.Type.ERROR, result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGraphCommandNotAllowed() {
        when(graphCommand.allow(eq(graphContext))).thenReturn(GRAPH_COMMAND_FAILED);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        verify(graphCommand, times(1)).allow(eq(graphContext));
        verify(canvasCommand, never()).allow(eq(canvasHandler));
        assertEquals(CommandResult.Type.ERROR, result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        verify(graphCommand, times(1)).execute(eq(graphContext));
        verify(canvasCommand, times(1)).execute(eq(canvasHandler));
        assertEquals(CommandResult.Type.INFO, result.getType());
        assertFalse(result.getViolations().iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteButCanvasCommandFailed() {
        when(canvasCommand.execute(eq(canvasHandler))).thenReturn(CANVAS_COMMAND_FAILED);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        verify(graphCommand, times(1)).execute(eq(graphContext));
        verify(graphCommand, times(1)).undo(eq(graphContext));
        verify(canvasCommand, times(1)).execute(eq(canvasHandler));
        assertEquals(CommandResult.Type.ERROR, result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteButGraphCommandFailed() {
        when(graphCommand.execute(eq(graphContext))).thenReturn(GRAPH_COMMAND_FAILED);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        verify(graphCommand, times(1)).execute(eq(graphContext));
        verify(graphCommand, never()).undo(eq(graphContext));
        verify(canvasCommand, never()).execute(eq(canvasHandler));
        assertEquals(CommandResult.Type.ERROR, result.getType());
    }

    private class AbstractCanvasGraphCommandStub extends AbstractCanvasGraphCommand {

        @Override
        protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
            return graphCommand;
        }

        @Override
        protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
            return canvasCommand;
        }
    }
}
