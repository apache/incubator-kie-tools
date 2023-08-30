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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutExecutor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LayoutHelperTest {

    private static final String ROOT_NODE_ID = "ROOT_NODE";

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private LayoutService layoutService;

    @Mock
    private LayoutExecutor layoutExecutor;

    @Mock
    private Node rootNode;

    @Mock
    private HasBounds rootNodeContent;

    @Mock
    private Node graphNode1;

    @Mock
    private HasBounds graphNode1Content;

    @Mock
    private Node graphNode2;

    @Mock
    private HasBounds graphNode2Content;

    @Mock
    private Metadata metadata;

    @Captor
    private ArgumentCaptor<Bounds> argumentCaptor;

    @Before
    public void setup() {
        final Bounds graphNode1Bounds = Bounds.create(1, 2, 3, 4);
        when(graphNode1.getContent()).thenReturn(graphNode1Content);
        when(graphNode1Content.getBounds()).thenReturn(graphNode1Bounds);

        final Bounds graphNode2Bounds = Bounds.create(1, 2, 3, 4);
        when(graphNode2.getContent()).thenReturn(graphNode2Content);
        when(graphNode2Content.getBounds()).thenReturn(graphNode2Bounds);

        final Bounds rootNodeBounds = Bounds.create(1, 2, 3, 4);
        when(rootNode.getContent()).thenReturn(rootNodeContent);
        when(rootNodeContent.getBounds()).thenReturn(rootNodeBounds);

        when(diagram.getMetadata()).thenReturn(metadata);
        when(rootNode.getUUID()).thenReturn(ROOT_NODE_ID);
        when(graphNode1.getUUID()).thenReturn("GRAPH_NODE_1");
        when(graphNode2.getUUID()).thenReturn("GRAPH_NODE_2");

        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn(ROOT_NODE_ID);
        when(layoutService.hasLayoutInformation(graph)).thenReturn(false);

        final GraphNodeStoreImpl store = new GraphNodeStoreImpl();
        store.add(rootNode);
        store.add(graphNode1);
        store.add(graphNode2);

        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(store);
    }

    @Test
    public void applyLayout() {

        final LayoutHelper helper = new LayoutHelper(layoutService);
        helper.applyLayout(diagram, layoutExecutor);
        verify(rootNodeContent).setBounds(argumentCaptor.capture());

        final Bounds bounds = argumentCaptor.getValue();

        isCloseToZero(bounds.getLowerRight());
        isCloseToZero(bounds.getUpperLeft());
    }

    @Test
    public void applyLayoutDoNotOverrideExistingLayout() {
        when(layoutService.hasLayoutInformation(graph)).thenReturn(true);
        final LayoutHelper helper = new LayoutHelper(layoutService);
        helper.applyLayout(diagram, layoutExecutor, false);

        verify(layoutExecutor, never()).applyLayout(any(), any(), any());
    }

    @Test
    public void applyLayoutOverrideExistingLayout() {
        when(layoutService.hasLayoutInformation(graph)).thenReturn(true);
        final LayoutHelper helper = new LayoutHelper(layoutService);
        helper.applyLayout(diagram, layoutExecutor, true);

        verify(layoutExecutor).applyLayout(any(), any(), any());
    }

    private static void isCloseToZero(final Bound bound) {
        assertEquals(0.0, bound.getX(), 0.01);
        assertEquals(0.0, bound.getY(), 0.01);
    }
}