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
import org.kie.workbench.common.dmn.client.session.BaseCommandsTest;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClearSessionCommandTest extends BaseCommandsTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

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
}
