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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.UndoableLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.mockito.Mock;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PerformAutomaticLayoutCommandTest {

    @Mock
    private LayoutService service;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private EditorSession editorSession;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private Diagram diagram;

    @Mock
    private UndoableLayoutExecutor executor;

    @Mock
    private EventSourceMock<LockRequiredEvent> locker;

    private PerformAutomaticLayoutCommand command;

    private LayoutHelper layoutHelper;

    @Before
    public void setup() {
        layoutHelper = spy(new LayoutHelper(service));
        command = spy(new PerformAutomaticLayoutCommand(layoutHelper, commandManager, locker) {

            protected AbstractCanvasHandler getCanvasHandler() {
                return canvasHandler;
            }

            protected EditorSession getSession() {
                return editorSession;
            }
        });
    }

    @Test
    public void testMakeExecutor() {

        final UndoableLayoutExecutor executor = command.makeExecutor();

        assertEquals(canvasHandler, executor.getCanvasHandler());
        assertEquals(commandManager, executor.getCommandManager());
    }

    @Test
    public void testExecute() {

        doReturn(diagram).when(command).getDiagram();
        doReturn(executor).when(command).makeExecutor();

        command.execute(callback);

        verify(layoutHelper).applyLayout(diagram, executor, true);
        verify(callback).onSuccess();
        verify(command).executeLock();
    }
}