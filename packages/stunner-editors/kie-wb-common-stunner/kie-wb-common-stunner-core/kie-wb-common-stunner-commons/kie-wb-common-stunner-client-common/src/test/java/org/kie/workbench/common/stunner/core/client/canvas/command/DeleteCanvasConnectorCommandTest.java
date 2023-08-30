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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteCanvasConnectorCommandTest extends AbstractCanvasCommandTest {

    private static final String EDGE_ID = "e1";
    private static final String SOURCE_ID = "s1";

    @Mock
    private Edge candidate;
    @Mock
    private Node source;
    @Mock
    private Node target;
    @Mock
    private ConnectorShape connectorShape;

    private DeleteCanvasConnectorCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getUUID()).thenReturn(EDGE_ID);
        when(source.getUUID()).thenReturn(SOURCE_ID);
        when(candidate.getSourceNode()).thenReturn(source);
        when(candidate.getTargetNode()).thenReturn(target);
        when(canvas.getShape(EDGE_ID)).thenReturn(connectorShape);
        this.tested = new DeleteCanvasConnectorCommand(candidate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).deregister(eq(candidate));
        verify(canvasHandler,
               times(1)).notifyCanvasElementUpdated(eq(source));
        verify(canvasHandler,
               times(1)).notifyCanvasElementUpdated(eq(target));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteNullSourceNode() {
        when(candidate.getSourceNode()).thenReturn(null);

        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).deregister(eq(candidate));
        verify(canvasHandler,
               never()).notifyCanvasElementUpdated(eq(source));
        verify(canvasHandler,
               times(1)).notifyCanvasElementUpdated(eq(target));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteNullTargetNode() {
        when(candidate.getTargetNode()).thenReturn(null);

        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).deregister(eq(candidate));
        verify(canvasHandler,
               times(1)).notifyCanvasElementUpdated(eq(source));
        verify(canvasHandler,
               never()).notifyCanvasElementUpdated(eq(target));
    }

    @Test
    public void testExecuteNullShape() {
        when(canvas.getShape(EDGE_ID)).thenReturn(null);
        assertFalse(CommandUtils.isError(tested.execute(canvasHandler)));
        verify(canvasHandler, never()).deregister(eq(candidate));
    }
}