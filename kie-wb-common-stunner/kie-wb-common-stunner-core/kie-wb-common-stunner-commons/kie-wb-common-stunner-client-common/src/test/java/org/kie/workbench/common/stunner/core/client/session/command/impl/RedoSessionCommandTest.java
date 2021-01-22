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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RedoCommandHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class RedoSessionCommandTest extends BaseSessionCommandKeyboardTest {

    @Mock
    private RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasHandlerImpl canvasHandler;

    @Mock
    private GraphsProvider graphsProvider;

    @Override
    protected AbstractClientSessionCommand<EditorSession> getCommand() {
        return new RedoSessionCommand(sessionCommandManager,
                                      redoCommandHandler,
                                      graphsProvider);
    }

    @Override
    protected KeyboardEvent.Key[] getExpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.CONTROL, KeyboardEvent.Key.SHIFT, KeyboardEvent.Key.Z};
    }

    @Override
    protected KeyboardEvent.Key[] getUnexpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC};
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyRedoSuccess() {
        RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager, redoCommandHandler, graphsProvider));

        doCallRealMethod().when(command).onCommandUndoExecuted(any(CanvasCommandUndoneEvent.class));
        doCallRealMethod().when(command).bind(any(EditorSession.class));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(keyboardControl.addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class))).thenReturn(keyboardControl);
        ((AbstractClientSessionCommand) command).bind(session);

        CanvasCommandUndoneEvent event = new CanvasCommandUndoneEvent(canvasHandler,
                                                                      new CompositeCommand(true),
                                                                      null);
        command.onCommandUndoExecuted(event);
        verify(redoCommandHandler, times(1)).onUndoCommandExecuted(event.getCommand());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyRedoFails() {
        RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager, redoCommandHandler, graphsProvider));

        doCallRealMethod().when(command).onCommandUndoExecuted(any(CanvasCommandUndoneEvent.class));
        doCallRealMethod().when(command).bind(any(EditorSession.class));

        when(session.getCanvasHandler()).thenReturn(new CanvasHandlerImpl(null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null));
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(keyboardControl.addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class))).thenReturn(keyboardControl);
        ((AbstractClientSessionCommand) command).bind(session);

        CanvasCommandUndoneEvent event = new CanvasCommandUndoneEvent(canvasHandler,
                                                                      new CompositeCommand(true),
                                                                      null);
        command.onCommandUndoExecuted(event);

        verify(redoCommandHandler, times(0)).onUndoCommandExecuted(event.getCommand());
    }

    @Test
    public void testNotifyRedoWhenIsNotBindToCurrentDiagram() {
        final RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager, redoCommandHandler, graphsProvider));

        doReturn(false).when(command).isBindToCurrentDiagram();
        doCallRealMethod().when(command).onCommandUndoExecuted(any(CanvasCommandUndoneEvent.class));
        doCallRealMethod().when(command).bind(any(EditorSession.class));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(keyboardControl.addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class))).thenReturn(keyboardControl);
        ((AbstractClientSessionCommand) command).bind(session);

        final CanvasCommandUndoneEvent event = new CanvasCommandUndoneEvent(canvasHandler,
                                                                            new CompositeCommand(true),
                                                                            null);
        command.onCommandUndoExecuted(event);
        verify(redoCommandHandler, never()).onUndoCommandExecuted(event.getCommand());
    }

    @Test
    public void testOnCommandExecutedSuccess() {
        RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager, redoCommandHandler, graphsProvider));

        doCallRealMethod().when(command).onCommandExecuted(any(CanvasCommandExecutedEvent.class));
        doCallRealMethod().when(command).bind(any(EditorSession.class));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(keyboardControl.addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class))).thenReturn(keyboardControl);
        ((AbstractClientSessionCommand) command).bind(session);

        CanvasCommandExecutedEvent event = new CanvasCommandExecutedEvent(canvasHandler,
                                                                          new CompositeCommand(true),
                                                                          null);
        command.onCommandExecuted(event);
        verify(redoCommandHandler, times(1)).onCommandExecuted(event.getCommand());
    }

    public void testOnCommandExecutedFails() {
        RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager, redoCommandHandler, graphsProvider));

        doCallRealMethod().when(command).onCommandExecuted(any(CanvasCommandExecutedEvent.class));
        doCallRealMethod().when(command).bind(any(EditorSession.class));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(keyboardControl.addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class))).thenReturn(keyboardControl);
        ((AbstractClientSessionCommand) command).bind(session);

        CanvasCommandExecutedEvent event = new CanvasCommandExecutedEvent(canvasHandler,
                                                                          new CompositeCommand(true),
                                                                          null);
        command.onCommandExecuted(event);
        verify(redoCommandHandler, times(0)).onCommandExecuted(event.getCommand());
    }

    @Test
    public void testOnCommandExecutedWhenIsNotBindToCurrentDiagram() {

        final RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager, redoCommandHandler, graphsProvider));

        doReturn(false).when(command).isBindToCurrentDiagram();

        doCallRealMethod().when(command).onCommandExecuted(any(CanvasCommandExecutedEvent.class));
        doCallRealMethod().when(command).bind(any(EditorSession.class));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(keyboardControl.addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class))).thenReturn(keyboardControl);
        ((AbstractClientSessionCommand) command).bind(session);

        final CanvasCommandExecutedEvent event = new CanvasCommandExecutedEvent(canvasHandler,
                                                                                new CompositeCommand(true),
                                                                                null);
        command.onCommandExecuted(event);

        verify(redoCommandHandler, never()).onCommandExecuted(event.getCommand());
    }

    @Test
    public void testOnCurrentRegistryChanged() {
        final CurrentRegistryChangedEvent event = mock(CurrentRegistryChangedEvent.class);
        ((RedoSessionCommand) command).onCurrentRegistryChanged(event);

        verify((RedoSessionCommand) command).checkState();
    }

    @Test
    public void testBind() {
        final RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager,
                                                                      redoCommandHandler,
                                                                      graphsProvider));
        doNothing().when(command).superBind(session);
        doNothing().when(command).bindCommand();
        final String currentDiagramId = "id";
        when(graphsProvider.getCurrentDiagramId()).thenReturn(currentDiagramId);

        command.bind(session);

        verify(command).superBind(session);
        verify(command).bindCommand();
        verify(command).checkState();
        assertEquals(currentDiagramId, command.getDiagramId());
    }

    @Test
    public void testIsBindToCurrentDiagram() {

        final String currentDiagramId = "id";
        when(graphsProvider.getCurrentDiagramId()).thenReturn(currentDiagramId);

        final RedoSessionCommand command = spy(new RedoSessionCommand(sessionCommandManager,
                                                                      redoCommandHandler,
                                                                      graphsProvider));

        doNothing().when(command).superBind(session);
        doNothing().when(command).bindCommand();

        command.bind(session);

        assertTrue(command.isBindToCurrentDiagram());

        when(graphsProvider.getCurrentDiagramId()).thenReturn("other diagram");

        assertFalse(command.isBindToCurrentDiagram());
    }

    @Test
    public void testExecute() {

        final CommandResult result = GraphCommandResultBuilder.SUCCESS;

        when(redoCommandHandler.isEnabled()).thenReturn(true);
        when(redoCommandHandler.execute(any(), any())).thenReturn(result);

        command.bind(session);

        command.execute();

        verify(redoCommandHandler).execute(any(), any());

        // One invocation for bind(session) and other for execute()
        verify(((RedoSessionCommand) command), times(2)).checkState();
        verify(selectionControl).clearSelection();
    }

    @Test
    public void testExecuteWhenIsNotBindToCurrentDiagram() {

        final CommandResult result = GraphCommandResultBuilder.SUCCESS;

        doReturn(false).when(((RedoSessionCommand) command)).isBindToCurrentDiagram();

        when(redoCommandHandler.isEnabled()).thenReturn(true);
        when(redoCommandHandler.execute(any(), any())).thenReturn(result);

        command.bind(session);

        command.execute();

        verify(redoCommandHandler, never()).execute(any(), any());
        verify(((RedoSessionCommand) command), times(1)).checkState();
        verify(selectionControl, never()).clearSelection();
    }
}
