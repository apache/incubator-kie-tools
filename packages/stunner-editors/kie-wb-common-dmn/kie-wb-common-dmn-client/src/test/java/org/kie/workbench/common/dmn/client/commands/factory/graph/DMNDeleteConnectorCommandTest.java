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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDeleteConnectorCommandTest {

    @Mock
    private Edge<? extends View, Node> edge;

    @Mock
    private DMNGraphsProvider graphsProvider;

    private Edge<? extends ViewConnector, Node> edgeParameter = mock(Edge.class);

    private DMNDeleteConnectorCommand command;

    @Before
    public void setup() {

        when(edgeParameter.getUUID()).thenReturn("uuid");
        when(edge.getUUID()).thenReturn("uuid");
        command = new DMNDeleteConnectorCommand(edge, graphsProvider);
    }

    @Test
    public void testGetSetConnectionSourceCommand() {

        final SetConnectionSourceNodeCommand setConnectionSourceCommand = command.getSetConnectionSourceCommand(edgeParameter);

        assertTrue(setConnectionSourceCommand instanceof DMNSetConnectionSourceNodeCommand);
        assertNull(setConnectionSourceCommand.getSourceNode());
        assertEquals(edgeParameter, setConnectionSourceCommand.getEdge());
        assertEquals(graphsProvider, ((DMNSetConnectionSourceNodeCommand) setConnectionSourceCommand).getGraphsProvider());
    }

    @Test
    public void testGetSetConnectionTargetCommand() {

        final SetConnectionTargetNodeCommand setConnectionTargetCommand = command.getSetConnectionTargetCommand(edgeParameter);

        assertTrue(setConnectionTargetCommand instanceof DMNSetConnectionTargetNodeCommand);
        assertNull(setConnectionTargetCommand.getSourceNode());
        assertEquals(edgeParameter, setConnectionTargetCommand.getEdge());
        assertEquals(graphsProvider, ((DMNSetConnectionTargetNodeCommand) setConnectionTargetCommand).getGraphsProvider());
    }
}
