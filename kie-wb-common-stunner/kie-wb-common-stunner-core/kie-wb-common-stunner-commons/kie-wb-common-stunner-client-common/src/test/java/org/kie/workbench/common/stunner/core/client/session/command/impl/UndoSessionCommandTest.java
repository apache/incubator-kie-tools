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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UndoSessionCommandTest extends BaseSessionCommandKeyboardTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;

    @Mock
    private CommandResult commandResult;

    @Mock
    private org.uberfire.mvp.Command statusCallback;

    private List commandHistory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        super.setup();
        when(sessionCommandManager.getRegistry()).thenReturn(commandRegistry);
        commandHistory = new ArrayList<>();
        when(commandRegistry.getCommandHistory()).thenReturn(commandHistory);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
    }

    @Override
    protected AbstractClientSessionCommand<EditorSession> getCommand() {
        return new UndoSessionCommand(sessionCommandManager);
    }

    @Override
    protected KeyboardEvent.Key[] getExpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.CONTROL, KeyboardEvent.Key.Z};
    }

    @Override
    protected KeyboardEvent.Key[] getUnexpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC};
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccess() {
        command.bind(session);
        when(sessionCommandManager.undo(canvasHandler)).thenReturn(null);

        command.execute(callback);

        verify(sessionCommandManager,
               times(1)).undo(canvasHandler);
        verify(selectionControl,
               times(1)).clearSelection();
        verify(callback,
               times(1)).onSuccess();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithErrors() {
        command.bind(session);
        when(sessionCommandManager.undo(canvasHandler)).thenReturn(commandResult);
        when(commandResult.getType()).thenReturn(CommandResult.Type.ERROR);

        command.execute(callback);

        verify(sessionCommandManager,
               times(1)).undo(canvasHandler);
        verify(selectionControl,
               times(1)).clearSelection();
        verify(callback,
               times(1)).onError(commandResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnCommandExecuted() {
        command.bind(session);
        command.listen(statusCallback);

        ((UndoSessionCommand) command).onCommandAdded(new RegisterChangedEvent());
        assertFalse(command.isEnabled());

        commandHistory.add(mock(Command.class));

        ((UndoSessionCommand) command).onCommandAdded(new RegisterChangedEvent());
        assertTrue(command.isEnabled());
        verify(statusCallback,
               times(2)).execute();
    }
}
