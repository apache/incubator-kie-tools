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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
public class VertexPositioningTest {

    @Test
    public void testRemoveToVirtualVertex() {
        //             Input: Vertex_01----VIRTUALVERTEX----Vertex_02
        //    Input Vertices: [Vertex_01,VIRTUALVERTEX,Vertex_02]
        //          Expected: Vertex_01----Vertex_02
        // Expected Vertices: [Vertex_01,Vertex_02]
        final Vertex virtual = new Vertex("V", true);
        final Vertex real_01 = new Vertex("01");
        final Vertex real_02 = new Vertex("02");

        final OrientedEdge edge1 = new OrientedEdgeImpl("01", "V");
        final OrientedEdge edge2 = new OrientedEdgeImpl("V", "02");

        final ArrayList<OrientedEdge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);

        final HashSet<Vertex> vertices = new HashSet<>();
        vertices.add(virtual);
        vertices.add(real_01);
        vertices.add(real_02);

        final DefaultVertexPositioning vertexPositioning = new DefaultVertexPositioning();
        vertexPositioning.removeVirtualVertex(edge1, edges, vertices);

        assertTrue(vertices.contains(real_01));
        assertTrue(vertices.contains(real_02));
        assertFalse(vertices.contains(virtual));
        assertFalse(edges.contains(edge1));
        assertFalse(edges.contains(edge2));
        assertEquals(1, edges.size());

        final OrientedEdge newEdge = edges.get(0);
        assertTrue(newEdge.isLinkedWithVertexId("01"));
        assertTrue(newEdge.isLinkedWithVertexId("02"));
    }

    @Test
    public void testRemoveFromVirtualVertex() {
        final Vertex virtual = new Vertex("V", true);
        final Vertex real_01 = new Vertex("01");
        final Vertex real_02 = new Vertex("02");

        final OrientedEdge edge1 = new OrientedEdgeImpl("01", "V");
        final OrientedEdge edge2 = new OrientedEdgeImpl("V", "02");

        final ArrayList<OrientedEdge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);

        final HashSet<Vertex> vertices = new HashSet<>();
        vertices.add(virtual);
        vertices.add(real_01);
        vertices.add(real_02);

        final DefaultVertexPositioning drawing = new DefaultVertexPositioning();
        drawing.removeVirtualVertex(edge2, edges, vertices);

        assertTrue(vertices.contains(real_01));
        assertTrue(vertices.contains(real_02));
        assertFalse(vertices.contains(virtual));
        assertFalse(edges.contains(edge1));
        assertFalse(edges.contains(edge2));
        assertEquals(1, edges.size());

        final OrientedEdge newEdge = edges.get(0);
        assertTrue(newEdge.isLinkedWithVertexId("01"));
        assertTrue(newEdge.isLinkedWithVertexId("02"));
    }

    @Test
    public void removeEdgeBetweenVirtualVertices() {

        // Concrete Vertices: C1,C2
        // Virtual: V1, V2, V3
        // Input: C1---->V1---->V2---->V3---->C2

        final Vertex c1 = new Vertex("C1");
        final Vertex c2 = new Vertex("C2");
        final Vertex v1 = new Vertex("V1", true);
        final Vertex v2 = new Vertex("V2", true);
        final Vertex v3 = new Vertex("V3", true);
        final HashSet<Vertex> inputVertex = new HashSet<>();
        inputVertex.add(c1);
        inputVertex.add(c2);
        inputVertex.add(v1);
        inputVertex.add(v2);
        inputVertex.add(v3);

        final ArrayList<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("C1", "V1"));
        edges.add(new OrientedEdgeImpl("V1", "V2"));

        final OrientedEdge betweenVirtualVertices = new OrientedEdgeImpl("V2", "V3");
        edges.add(betweenVirtualVertices);

        edges.add(new OrientedEdgeImpl("V3", "C2"));

        final DefaultVertexPositioning vertexPositioning = new DefaultVertexPositioning();
        vertexPositioning.removeVirtualVertex(betweenVirtualVertices, edges, inputVertex);

        assertTrue(inputVertex.contains(c1));
        assertTrue(inputVertex.contains(c2));
        assertTrue(inputVertex.contains(v1));
        assertFalse(inputVertex.contains(v2));
        assertFalse(inputVertex.contains(v3));
        assertFalse(edges.contains(betweenVirtualVertices));
        assertEquals(2, edges.size());
        assertEquals(edges.get(0).getFromVertexId(), "C1");
        assertEquals(edges.get(0).getToVertexId(), "V1");
        assertEquals(edges.get(1).getFromVertexId(), "V1");
        assertEquals(edges.get(1).getToVertexId(), "C2");

        // Input: C1---->V1---->V2---->V3---->C2
        // Expected: C1---->V1---->C2

    }

    @Test
    public void removeAllVirtualVertices() {
        // Input: A---->V1---->V2---->V3---->V4--->B--->C--->V5--->V6--->D--->V7--->V8--->E
        final Vertex[] vertices = new Vertex[]{
                new Vertex("A"),
                new Vertex("V1", true),
                new Vertex("V2", true),
                new Vertex("V3", true),
                new Vertex("V4", true),
                new Vertex("B"),
                new Vertex("C"),
                new Vertex("V5", true),
                new Vertex("V6", true),
                new Vertex("D"),
                new Vertex("V7", true),
                new Vertex("V8", true),
                new Vertex("E")
        };

        final ArrayList<OrientedEdge> edges = connect(vertices);

        // Expected: A--->B--->C--->D--->E
        final DefaultVertexPositioning drawing = new DefaultVertexPositioning();
        final HashSet<Vertex> array = new HashSet<>(Arrays.asList(vertices));
        drawing.removeVirtualVertices(edges, array);

        assertEquals(4, edges.size());
        assertTrue(edges.contains(new OrientedEdgeImpl("A", "B")));
        assertTrue(edges.contains(new OrientedEdgeImpl("B", "C")));
        assertTrue(edges.contains(new OrientedEdgeImpl("C", "D")));
        assertTrue(edges.contains(new OrientedEdgeImpl("D", "E")));

        assertEquals(5, array.size());
        assertArrayEquals(new String[]{"A", "B", "C", "D", "E"},
                          array.stream().map(Vertex::getId).sorted().toArray());
    }

    private ArrayList<OrientedEdge> connect(final Vertex... vertices) {
        final ArrayList<OrientedEdge> edges = new ArrayList<>();
        for (int i = 0; i < vertices.length; i++) {

            if (i + 1 < vertices.length) {
                final OrientedEdge e = new OrientedEdgeImpl(vertices[i].getId(), vertices[i + 1].getId());
                edges.add(e);
            }
        }
        return edges;
    }
}