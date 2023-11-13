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
public class AddConnectorCommandTest extends AbstractCanvasCommandTest {

    private static final String EDGE_ID = "e1";
    private static final String SOURCE_ID = "s1";

    @Mock
    private Edge candidate;
    @Mock
    private Node source;
    @Mock
    private Connection connection;

    private AddConnectorCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getUUID()).thenReturn(EDGE_ID);
        when(source.getUUID()).thenReturn(SOURCE_ID);
        when(candidate.getSourceNode()).thenReturn(source);
        this.tested = new AddConnectorCommand(source,
                                              candidate,
                                              connection,
                                              SHAPE_SET_ID);
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.AddConnectorCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.AddConnectorCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(candidate,
                     graphCommand.getEdge());
        assertEquals(source,
                     graphCommand.getSourceNode());
        assertEquals(connection,
                     graphCommand.getConnection());
    }

    @Test
    public void testGetCanvasCommand() {
        final AddCanvasConnectorCommand canvasCommand =
                (AddCanvasConnectorCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(candidate,
                     canvasCommand.getCandidate());
        assertEquals(SHAPE_SET_ID,
                     canvasCommand.getShapeSetId());
    }
}
