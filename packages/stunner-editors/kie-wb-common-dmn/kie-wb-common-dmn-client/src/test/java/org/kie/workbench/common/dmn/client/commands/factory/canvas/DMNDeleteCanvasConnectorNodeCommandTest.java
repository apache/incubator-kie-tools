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
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDeleteCanvasConnectorNodeCommandTest {

    @Mock
    private DMNGraphsProvider graphsProvider;

    @Mock
    private Edge candidate;

    @Mock
    private AbstractCanvasHandler context;

    private DMNDeleteCanvasConnectorNodeCommand command;

    @Before
    public void setup() {
        command = spy(new DMNDeleteCanvasConnectorNodeCommand(candidate, graphsProvider));
    }

    @Test
    public void testUndoWhenCandidateDoesNotBelongsToCurrentGraph() {

        doReturn(false).when(command).candidateNodeBelongsToCurrentGraph();

        final CommandResult<CanvasViolation> commandResult = command.undo(context);

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(command, never()).superUndo(context);
    }

    @Test
    public void testUndoWhenCandidateDoesBelongsToCurrentGraph() {

        final CommandResult superCommandResult = mock(CommandResult.class);
        doReturn(superCommandResult).when(command).superUndo(context);
        doReturn(true).when(command).candidateNodeBelongsToCurrentGraph();

        final CommandResult<CanvasViolation> commandResult = command.undo(context);

        assertEquals(superCommandResult, commandResult);

        verify(command).superUndo(context);
    }
}
