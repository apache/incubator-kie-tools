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
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This class tests SessionCommandManagerImpl. The goal for the session command
 * manager is just update the session's registry if the command executions are successful,
 * otherwise should not add the commands on the registry.
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionCommandManagerTest {

    @Mock
    SessionManager clientSessionManager;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    AbstractCanvas canvas;
    @Mock
    EditorSession editorSession;
    @Mock
    Command<AbstractCanvasHandler, CanvasViolation> command;
    @Mock
    CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;

    private SessionCommandManagerImpl tested;

    @Before
    public void setup() throws Exception {
        CanvasCommandManagerImpl commandManager = new CanvasCommandManagerImpl();
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(clientSessionManager.getCurrentSession()).thenReturn(editorSession);
        when(editorSession.getCommandRegistry()).thenReturn(commandRegistry);
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerImpl(clientSessionManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccess() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.execute(canvasHandler,
                       command);
        verify(commandRegistry,
               times(1)).register(command);
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteFailed() {
        when(command.execute(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.FAILED);
        tested.execute(canvasHandler,
                       command);
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
        verify(commandRegistry,
               times(0)).pop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoSuccess() {
        when(command.undo(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.undo(canvasHandler,
                    command);
        verify(commandRegistry,
               times(1)).pop();
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoFailed() {
        when(command.undo(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.FAILED);
        tested.undo(canvasHandler,
                    command);
        verify(commandRegistry,
               times(0)).pop();
        verify(commandRegistry,
               times(0)).register(command);
        verify(commandRegistry,
               times(0)).peek();
    }
}
