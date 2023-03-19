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

package com.ait.lienzo.client.core.layout.sugiyama.step04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.ait.lienzo.client.core.layout.OrientedEdgeImpl;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.sugiyama.OrientedEdge;
import org.junit.Test;

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
        final VertexPosition virtual = new VertexPosition("V", true);
        final VertexPosition real_01 = new VertexPosition("01");
        final VertexPosition real_02 = new VertexPosition("02");

        final OrientedEdge edge1 = new OrientedEdgeImpl("01", "V");
        final OrientedEdge edge2 = new OrientedEdgeImpl("V", "02");

        final ArrayList<OrientedEdge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);

        final HashSet<VertexPosition> vertices = new HashSet<>();
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
        final VertexPosition virtual = new VertexPosition("V", true);
        final VertexPosition real_01 = new VertexPosition("01");
        final VertexPosition real_02 = new VertexPosition("02");

        final OrientedEdge edge1 = new OrientedEdgeImpl("01", "V");
        final OrientedEdge edge2 = new OrientedEdgeImpl("V", "02");

        final ArrayList<OrientedEdge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);

        final HashSet<VertexPosition> vertices = new HashSet<>();
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

        final VertexPosition c1 = new VertexPosition("C1");
        final VertexPosition c2 = new VertexPosition("C2");
        final VertexPosition v1 = new VertexPosition("V1", true);
        final VertexPosition v2 = new VertexPosition("V2", true);
        final VertexPosition v3 = new VertexPosition("V3", true);
        final HashSet<VertexPosition> inputVertex = new HashSet<>();
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
        final VertexPosition[] vertices = new VertexPosition[]{
                new VertexPosition("A"),
                new VertexPosition("V1", true),
                new VertexPosition("V2", true),
                new VertexPosition("V3", true),
                new VertexPosition("V4", true),
                new VertexPosition("B"),
                new VertexPosition("C"),
                new VertexPosition("V5", true),
                new VertexPosition("V6", true),
                new VertexPosition("D"),
                new VertexPosition("V7", true),
                new VertexPosition("V8", true),
                new VertexPosition("E")
        };

        final ArrayList<OrientedEdge> edges = connect(vertices);

        // Expected: A--->B--->C--->D--->E
        final DefaultVertexPositioning drawing = new DefaultVertexPositioning();
        final HashSet<VertexPosition> array = new HashSet<>(Arrays.asList(vertices));
        drawing.removeVirtualVertices(edges, array);

        assertEquals(4, edges.size());
        assertTrue(edges.contains(new OrientedEdgeImpl("A", "B")));
        assertTrue(edges.contains(new OrientedEdgeImpl("B", "C")));
        assertTrue(edges.contains(new OrientedEdgeImpl("C", "D")));
        assertTrue(edges.contains(new OrientedEdgeImpl("D", "E")));

        assertEquals(5, array.size());
        assertArrayEquals(new String[]{"A", "B", "C", "D", "E"},
                          array.stream().map(VertexPosition::getId).sorted().toArray());
    }

    private ArrayList<OrientedEdge> connect(final VertexPosition... vertices) {
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
