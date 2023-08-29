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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDeleteCanvasNodeCommandTest {

    @Mock
    private Node candidate;

    @Mock
    private DMNGraphsProvider graphsProvide;

    private DMNDeleteCanvasNodeCommand command;

    @Before
    public void setup() {

        command = spy(new DMNDeleteCanvasNodeCommand(candidate, graphsProvide));
    }

    @Test
    public void testCreateUndoCommandWhenBelongsToCurrentGraph() {

        final AbstractCanvasCommand superCanvasCommand = mock(AbstractCanvasCommand.class);
        final Node parent = mock(Node.class);
        final String ssid = "ssid";

        doReturn(true).when(command).belongsToCurrentGraph(candidate);
        doReturn(superCanvasCommand).when(command).superCreateUndoCommand(parent, candidate, ssid);

        final AbstractCanvasCommand actualUndoCommand = command.createUndoCommand(parent, candidate, ssid);

        assertEquals(superCanvasCommand, actualUndoCommand);
        verify(command, never()).createEmptyCommand();
    }

    @Test
    public void testCreateUndoCommandWhenDoesNotBelongsToCurrentGraph() {

        final Node parent = mock(Node.class);
        final String ssid = "ssid";

        doReturn(false).when(command).belongsToCurrentGraph(candidate);
        final AbstractCanvasCommand emptyCommand = mock(AbstractCanvasCommand.class);
        doReturn(emptyCommand).when(command).createEmptyCommand();

        final AbstractCanvasCommand actualUndoCommand = command.createUndoCommand(parent, candidate, ssid);

        assertEquals(emptyCommand, actualUndoCommand);
        verify(command).createEmptyCommand();
        verify(command, never()).superCreateUndoCommand(parent, candidate, ssid);
    }

    @Test
    public void testEmptyCommand() {

        final AbstractCanvasCommand emptyCommand = command.createEmptyCommand();
        final AbstractCanvasHandler handler = mock(AbstractCanvasHandler.class);

        assertEquals(CanvasCommandResultBuilder.SUCCESS, emptyCommand.execute(handler));
        assertEquals(CanvasCommandResultBuilder.SUCCESS, emptyCommand.undo(handler));
    }
}
