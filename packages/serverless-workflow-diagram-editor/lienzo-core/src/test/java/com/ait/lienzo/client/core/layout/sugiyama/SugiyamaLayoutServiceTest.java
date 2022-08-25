/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.layout.sugiyama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ait.lienzo.client.core.layout.Layout;
import com.ait.lienzo.client.core.layout.graph.Vertex;
import com.ait.lienzo.client.core.layout.sugiyama.step01.CycleBreaker;
import com.ait.lienzo.client.core.layout.sugiyama.step02.VertexLayerer;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VertexOrdering;
import com.ait.lienzo.client.core.layout.sugiyama.step04.VertexPositioning;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.ait.lienzo.client.core.layout.sugiyama.SugiyamaLayoutService.DEFAULT_LAYER_ARRANGEMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SugiyamaLayoutServiceTest {

    @Mock
    private CycleBreaker cycleBreaker;

    @Mock
    private VertexLayerer vertexLayerer;

    @Mock
    private VertexOrdering vertexOrdering;

    @Mock
    private VertexPositioning vertexPositioning;

    private SugiyamaLayoutService layoutService;

    @Before
    public void setup() {

        layoutService = spy(new SugiyamaLayoutService(cycleBreaker,
                                                      vertexLayerer,
                                                      vertexOrdering,
                                                      vertexPositioning));
    }

    @Test
    public void testCreateLayout() {

        final String endingVertexId = "ending";
        final String startingVertexId = "starting";
        final HashMap indexByUuid = mock(HashMap.class);
        final Collection values = mock(Collection.class);
        final LayeredGraph layeredGraph = mock(LayeredGraph.class);
        final List layers = mock(List.class);
        final List<Vertex> vertices = new ArrayList<>();
        final Layout layout = mock(Layout.class);
        final InOrder inOrder = inOrder(cycleBreaker, vertexLayerer,
                                        vertexOrdering, vertexPositioning, layoutService);

        when(indexByUuid.values()).thenReturn(values);
        when(layeredGraph.getLayers()).thenReturn(layers);
        doReturn(indexByUuid).when(layoutService).createIndex(vertices);
        doReturn(layeredGraph).when(layoutService).createLayeredGraph(vertices, startingVertexId, endingVertexId);
        doNothing().when(layoutService).createEdges(layeredGraph, indexByUuid);

        doReturn(layout).when(layoutService).buildLayout(indexByUuid, layeredGraph);


        final Layout createdLayout = layoutService.createLayout(vertices, startingVertexId, endingVertexId);

        inOrder.verify(cycleBreaker).breakCycle(layeredGraph);
        inOrder.verify(vertexLayerer).createLayers(layeredGraph);
        inOrder.verify(vertexOrdering).orderVertices(layeredGraph);
        inOrder.verify(layoutService).createEdges(layeredGraph, indexByUuid);
        inOrder.verify(vertexPositioning).calculateVerticesPositions(layeredGraph,
                                                                     DEFAULT_LAYER_ARRANGEMENT);

        assertEquals(layout, createdLayout);
        verify(layoutService).buildLayout(indexByUuid, layeredGraph);
    }

    @Test
    public void testCreateIndex() {

        final String id1 = "id1";
        final String id2 = "id2";
        final Vertex n1 = new Vertex(id1);
        final Vertex n2 = new Vertex(id2);
        final List<Vertex> nodes = Arrays.asList(n1, n2);

        final HashMap<String, Vertex> index = layoutService.createIndex(nodes);

        assertTrue(index.containsKey(id1));
        assertTrue(index.containsKey(id2));

        assertEquals(n1, index.get(id1));
        assertEquals(n2, index.get(id2));
    }
}
