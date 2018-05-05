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
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandListener;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.HasCommandListener;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This test creates an stub for AbstractSessionCommandManager in order to
 * test its abstract behaviors.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSessionCommandManagerTest {

    private class SessionCommandManagerStub
            extends AbstractSessionCommandManager {

        @Override
        protected SessionManager getClientSessionManager() {
            return clientSessionManager;
        }

        @Override
        protected CommandListener<AbstractCanvasHandler, CanvasViolation> getRegistryListener() {
            return commandListener;
        }
    }

    private class SuccessCanvasCommandManager extends CanvasCommandManagerStub {

        @Override
        protected CommandResult<CanvasViolation> getResult() {
            return CanvasCommandResultBuilder.SUCCESS;
        }
    }

    private class CommandExceptionCanvasCommandManager extends CanvasCommandManagerStub {

        @Override
        protected CommandResult<CanvasViolation> getResult() {
            throw new CommandException(command);
        }
    }

    private class RuntimeExceptionCanvasCommandManager extends CanvasCommandManagerStub {

        @Override
        protected CommandResult<CanvasViolation> getResult() {
            throw new RuntimeException();
        }
    }

    private abstract class CanvasCommandManagerStub
            implements CanvasCommandManager<AbstractCanvasHandler>,
                       HasCommandListener<CommandListener<AbstractCanvasHandler, CanvasViolation>> {

        CommandListener<AbstractCanvasHandler, CanvasViolation> listener;

        protected abstract CommandResult<CanvasViolation> getResult();

        @Override
        public CommandResult<CanvasViolation> allow(AbstractCanvasHandler context,
                                                    Command<AbstractCanvasHandler, CanvasViolation> command) {
            return getResult();
        }

        @Override
        public CommandResult<CanvasViolation> execute(AbstractCanvasHandler context,
                                                      Command<AbstractCanvasHandler, CanvasViolation> command) {
            return getResult();
        }

        @Override
        public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context,
                                                   Command<AbstractCanvasHandler, CanvasViolation> command) {
            return getResult();
        }

        @Override
        public void setCommandListener(CommandListener<AbstractCanvasHandler, CanvasViolation> listener) {
            this.listener = listener;
        }
    }

    @Mock
    SessionManager clientSessionManager;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    EditorSession editorSession;
    @Mock
    Command<AbstractCanvasHandler, CanvasViolation> command;
    @Mock
    CommandListener<AbstractCanvasHandler, CanvasViolation> commandListener;
    @Mock
    CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;

    private SessionCommandManagerStub tested;

    @Before
    public void setup() throws Exception {
        when(clientSessionManager.getCurrentSession()).thenReturn(editorSession);
        when(editorSession.getCommandRegistry()).thenReturn(commandRegistry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccess() {
        SuccessCanvasCommandManager commandManager =
                new SuccessCanvasCommandManager();
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerStub();
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler,
                                                               mock(Command.class));
        assertNotNull(result);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        assertEquals(commandListener,
                     commandManager.listener);
        verify(clientSessionManager,
               times(0)).handleClientError(any(ClientRuntimeError.class));
        verify(clientSessionManager,
               times(0)).handleCommandError(any(CommandException.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteCommandException() {
        CommandExceptionCanvasCommandManager commandManager =
                new CommandExceptionCanvasCommandManager();
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerStub();
        tested.execute(canvasHandler, mock(Command.class));
        assertEquals(commandListener,
                     commandManager.listener);
        verify(clientSessionManager,
               times(0)).handleClientError(any(ClientRuntimeError.class));
        verify(clientSessionManager,
               times(1)).handleCommandError(any(CommandException.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteClientError() {
        RuntimeExceptionCanvasCommandManager commandManager =
                new RuntimeExceptionCanvasCommandManager();
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerStub();
        tested.execute(canvasHandler,
                       mock(Command.class));
        assertEquals(commandListener,
                     commandManager.listener);
        verify(clientSessionManager,
               times(1)).handleClientError(any(ClientRuntimeError.class));
        verify(clientSessionManager,
               times(0)).handleCommandError(any(CommandException.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndoSuccess() {
        SuccessCanvasCommandManager commandManager =
                new SuccessCanvasCommandManager();
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerStub();
        CommandResult<CanvasViolation> result = tested.undo(canvasHandler,
                                                            mock(Command.class));
        assertNotNull(result);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        assertEquals(commandListener,
                     commandManager.listener);
        verify(clientSessionManager,
               times(0)).handleClientError(any(ClientRuntimeError.class));
        verify(clientSessionManager,
               times(0)).handleCommandError(any(CommandException.class));
    }

    @Test(expected = CommandException.class)
    @SuppressWarnings("unchecked")
    public void testUndoCommandException() {
        CommandExceptionCanvasCommandManager commandManager =
                new CommandExceptionCanvasCommandManager();
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerStub();
        tested.undo(canvasHandler,
                    mock(Command.class));
        assertEquals(commandListener,
                     commandManager.listener);
        verify(clientSessionManager,
               times(0)).handleClientError(any(ClientRuntimeError.class));
        verify(clientSessionManager,
               times(1)).handleCommandError(any(CommandException.class));
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void testUndoClientError() {
        RuntimeExceptionCanvasCommandManager commandManager =
                new RuntimeExceptionCanvasCommandManager();
        when(editorSession.getCommandManager()).thenReturn(commandManager);
        this.tested = new SessionCommandManagerStub();
        tested.undo(canvasHandler,
                    mock(Command.class));
        assertEquals(commandListener,
                     commandManager.listener);
        verify(clientSessionManager,
               times(1)).handleClientError(any(ClientRuntimeError.class));
        verify(clientSessionManager,
               times(0)).handleCommandError(any(CommandException.class));
    }
}
