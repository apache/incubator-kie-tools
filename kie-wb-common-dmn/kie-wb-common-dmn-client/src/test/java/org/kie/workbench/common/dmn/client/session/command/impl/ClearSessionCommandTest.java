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

package org.kie.workbench.common.dmn.client.session.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClearSessionCommandTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private org.uberfire.mvp.Command callback;

    private ClearSessionCommand command;

    @Before
    public void setup() {
        this.command = new ClearSessionCommand(canvasCommandFactory,
                                               sessionCommandManager);
        this.command.listen(callback);
    }

    @Test
    public void checkExecutionCommands() {
        command.onCommandExecuted(makeCommandExecutionContext(new MockCommand()));

        verify(callback).execute();
    }

    @Test
    public void checkVetoExecutionCommands() {
        command.onCommandExecuted(makeCommandExecutionContext(new MockVetoExecutionCommand()));

        verify(callback,
               never()).execute();
    }

    @Test
    public void checkUndoCommands() {
        command.onCommandUndoExecuted(makeCommandUndoContext(new MockCommand()));

        verify(callback).execute();
    }

    @Test
    public void checkVetoUndoCommands() {
        command.onCommandUndoExecuted(makeCommandUndoContext(new MockVetoUndoCommand()));

        verify(callback,
               never()).execute();
    }

    @SuppressWarnings("unchecked")
    private CanvasCommandExecutedEvent makeCommandExecutionContext(final Command command) {
        return new CanvasCommandExecutedEvent(canvasHandler,
                                              command,
                                              CanvasCommandResultBuilder.SUCCESS);
    }

    @SuppressWarnings("unchecked")
    private CanvasUndoCommandExecutedEvent makeCommandUndoContext(final Command command) {
        return new CanvasUndoCommandExecutedEvent(canvasHandler,
                                                  command,
                                                  CanvasCommandResultBuilder.SUCCESS);
    }

    private static class MockCommand extends AbstractCanvasGraphCommand {

        @Override
        protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
            return null;
        }

        @Override
        protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
            return null;
        }
    }

    private static class MockVetoExecutionCommand extends MockCommand implements VetoExecutionCommand {

    }

    private static class MockVetoUndoCommand extends MockCommand implements VetoUndoCommand {

    }
}
