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

package org.kie.workbench.common.stunner.core.client.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.command.ContextualGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This class tests RequestCommandManagerTest. The goal for the request command
 * manager is just update the session's registry if the command executions are successful, but just
 * using a single composite commands once the current request (user interaction) ends.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestCommandManagerTest {

    @Mock
    private SessionManager clientSessionManager;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AbstractCanvas canvas;
    @Mock
    private EditorSession editorSession;
    @Mock
    private ContextualGraphCommandExecutionContext executionContext;
    @Mock
    private Command<AbstractCanvasHandler, CanvasViolation> command;
    @Mock
    private Command<AbstractCanvasHandler, CanvasViolation> command1;
    @Mock
    private Command<AbstractCanvasHandler, CanvasViolation> command2;
    @Mock
    private CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;
    @Mock
    private CanvasMouseDownEvent mouseDownEvent;
    @Mock
    private CanvasMouseUpEvent mouseUpEvent;

    private RequestCommandManager tested;
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        commandManager = new CanvasCommandManagerImpl<>();
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(mock(Index.class));
        when(canvasHandler.getGraphExecutionContext()).thenReturn(executionContext);
        when(clientSessionManager.getCurrentSession()).thenReturn(editorSession);
        when(editorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(editorSession.getCommandRegistry()).thenReturn(commandRegistry);
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new RequestCommandManager(clientSessionManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleExecuteSuccess() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.execute(canvasHandler,
                       command);
        tested.onCanvasMouseUpEvent(mouseUpEvent);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandRegistry,
               times(1)).register(commandArgumentCaptor.capture());
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
        assertCompositeCommand(commandArgumentCaptor.getValue(),
                               1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleExecuteSuccess() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        when(command1.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        when(command2.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.execute(canvasHandler,
                       command);
        tested.execute(canvasHandler,
                       command1);
        tested.execute(canvasHandler,
                       command2);
        tested.onCanvasMouseUpEvent(mouseUpEvent);
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandRegistry,
               times(1)).register(commandArgumentCaptor.capture());
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
        assertCompositeCommand(commandArgumentCaptor.getValue(),
                               3);
    }

    @Test
    public void testSingleExecuteFailed() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.FAILED);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.execute(canvasHandler,
                       command);
        tested.onCanvasMouseUpEvent(mouseUpEvent);
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
    }

    @Test
    public void testMultipleExecuteFailed() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        when(command1.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        when(command2.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.FAILED);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.execute(canvasHandler,
                       command);
        tested.execute(canvasHandler,
                       command1);
        tested.execute(canvasHandler,
                       command2);
        tested.onCanvasMouseUpEvent(mouseUpEvent);
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
    }

    @Test
    public void testSingleUndoSuccess() {
        when(command.undo(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.undo(canvasHandler,
                    command);
        tested.onCanvasMouseUpEvent(mouseUpEvent);
        verify(commandRegistry,
               times(1)).pop();
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
    }

    @Test
    public void testSingleUndoFailed() {
        when(command.undo(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.FAILED);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.undo(canvasHandler,
                    command);
        tested.onCanvasMouseUpEvent(mouseUpEvent);
        verify(commandRegistry,
               times(0)).pop();
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoRequestCompleted() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.onCanvasMouseDownEvent(mouseDownEvent);
        tested.execute(canvasHandler,
                       command);
        verify(commandRegistry,
               times(0)).register(any(Command.class));
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
    }

    private void assertCompositeCommand(Command captured,
                                        int size) {
        assertNotNull(captured);
        assertTrue(captured instanceof CompositeCommand);
        assertFalse(((CompositeCommand) captured).isUndoReverse());
        assertEquals(size,
                     ((CompositeCommand) captured).size());
    }
}
