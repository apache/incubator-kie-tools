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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SetConnectionSourceNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node node;
    @Mock
    private Edge edge;
    @Mock
    private Connection connection;

    private SetConnectionSourceNodeCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        when(edge.getUUID()).thenReturn("e1");
        when(node.getUUID()).thenReturn("n1");
        this.tested = new SetConnectionSourceNodeCommand(node,
                                                         edge,
                                                         connection);
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(edge,
                     graphCommand.getEdge());
        assertEquals(node,
                     graphCommand.getSourceNode());
        assertEquals(connection,
                     graphCommand.getConnection());
    }

    @Test
    public void testGetCanvasCommand() {
        final SetCanvasConnectionCommand canvasCommand =
                (SetCanvasConnectionCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(edge,
                     canvasCommand.getEdge());
    }
}
