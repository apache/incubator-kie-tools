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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.ArrayList;
import java.util.List;

import org.appformer.client.stateControl.registry.Registry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UndoSessionCommandTest extends BaseSessionCommandKeyboardTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private Registry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;

    @Mock
    private CommandResult commandResult;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private org.uberfire.mvp.Command statusCallback;

    private List commandHistory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        super.setup();
        when(session.getCommandRegistry()).thenReturn(commandRegistry);
        commandHistory = new ArrayList<>();
        when(commandRegistry.getHistory()).thenReturn(commandHistory);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getCommandRegistry()).thenReturn(commandRegistry);
        when(sessionManager.getCurrentSession()).thenReturn(session);
    }

    @Override
    protected AbstractClientSessionCommand<EditorSession> getCommand() {
        return new UndoSessionCommand(sessionCommandManager, sessionManager);
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
        when(commandRegistry.isEmpty()).thenReturn(false);
        command.bind(session);
        when(sessionCommandManager.undo(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);

        command.execute(callback);

        verify(sessionCommandManager,
               times(1)).undo(eq(canvasHandler));
        verify(selectionControl,
               times(1)).clearSelection();
        verify(callback,
               times(1)).onSuccess();
        verify(commandRegistry,
               never()).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithErrors() {
        when(commandRegistry.isEmpty()).thenReturn(false);
        command.bind(session);
        when(sessionCommandManager.undo(eq(canvasHandler))).thenReturn(commandResult);
        when(commandResult.getType()).thenReturn(CommandResult.Type.ERROR);

        command.execute(callback);

        verify(sessionCommandManager,
               times(1)).undo(eq(canvasHandler));
        verify(selectionControl,
               times(1)).clearSelection();
        verify(callback,
               times(1)).onError(commandResult);
        verify(commandRegistry,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnCommandExecuted() {
        command.bind(session);
        command.listen(statusCallback);

        ((UndoSessionCommand) command).onCommandAdded(new RegisterChangedEvent(canvasHandler));
        assertFalse(command.isEnabled());

        commandHistory.add(mock(Command.class));

        ((UndoSessionCommand) command).onCommandAdded(new RegisterChangedEvent(canvasHandler));
        assertTrue(command.isEnabled());
        verify(statusCallback,
               atLeastOnce()).execute();
        verify(commandRegistry,
               never()).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnCommandExecutedCheckWrongSession() {
        command.bind(session);
        command.listen(statusCallback);

        assertFalse(command.isEnabled());
        verify(statusCallback,
               never()).execute();
        verify(commandRegistry,
               never()).clear();
    }

    @Test
    public void testOnCurrentRegistryChanged() {
        final CurrentRegistryChangedEvent event = mock(CurrentRegistryChangedEvent.class);
        ((UndoSessionCommand) command).onCurrentRegistryChanged(event);

        verify((UndoSessionCommand) command).checkState();
    }
}
