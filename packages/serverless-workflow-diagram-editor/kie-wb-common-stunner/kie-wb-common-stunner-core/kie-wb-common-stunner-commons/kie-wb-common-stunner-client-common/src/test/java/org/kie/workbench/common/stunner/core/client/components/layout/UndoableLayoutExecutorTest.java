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


package org.kie.workbench.common.stunner.core.client.components.layout;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layout;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class UndoableLayoutExecutorTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private CanvasCommandManager commandManager;

    @Mock
    private Layout layout;

    @Mock
    private Graph graph;

    @Mock
    private VertexPosition v1;

    @Mock
    private VertexPosition v2;

    @Mock
    private List<VertexPosition> fakeList;

    @Mock
    private Node node;

    private UndoableLayoutExecutor executor;

    @Before
    public void setup() {
        initMocks(this);

        when(fakeList.size()).thenReturn(2);
        when(layout.getNodePositions()).thenReturn(fakeList);
        when(v1.getId()).thenReturn("id");
        when(v2.getId()).thenReturn("id");
        when(fakeList.get(0)).thenReturn(v1);
        when(fakeList.get(1)).thenReturn(v2);

        when(graph.getNode(any())).thenReturn(node);

        executor = spy(new UndoableLayoutExecutor(canvasHandler, commandManager));
    }

    @Test
    public void testApplyLayout() {
        executor.applyLayout(layout, graph);

        verify(executor).createCommand(layout, graph);
        verify(commandManager).execute(any(), any());
    }

    @Test
    public void testCreateCommand() {
        executor.createCommand(layout, graph);
        verify(fakeList).get(0);
        verify(fakeList).get(1);
        verify(graph, times(2)).getNode("id");
    }
}
