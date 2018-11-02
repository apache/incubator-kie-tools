/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.canvas.util;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasHighlightVisitorTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testPrepareVisit() throws Exception {
        final Node root = new NodeImpl(UUID.randomUUID().toString());
        final Node parent1 = new NodeImpl(UUID.randomUUID().toString());
        final Node parent2 = new NodeImpl(UUID.randomUUID().toString());
        final Node parent3 = new NodeImpl(UUID.randomUUID().toString());
        final Node child1 = new NodeImpl(UUID.randomUUID().toString());
        final Node child2 = new NodeImpl(UUID.randomUUID().toString());
        final Node child3 = new NodeImpl(UUID.randomUUID().toString());

        final Edge parentEdge1 = new EdgeImpl<>(UUID.randomUUID().toString());
        final Edge parentEdge2 = new EdgeImpl<>(UUID.randomUUID().toString());
        final Edge parentEdge3 = new EdgeImpl<>(UUID.randomUUID().toString());
        final Edge childEdge1 = new EdgeImpl<>(UUID.randomUUID().toString());
        final Edge childEdge2 = new EdgeImpl<>(UUID.randomUUID().toString());
        final Edge childEdge3 = new EdgeImpl<>(UUID.randomUUID().toString());

        parentEdge1.setSourceNode(root);
        parentEdge1.setTargetNode(parent1);
        parentEdge2.setSourceNode(root);
        parentEdge2.setTargetNode(parent2);
        parentEdge3.setSourceNode(root);
        parentEdge3.setTargetNode(parent3);
        childEdge1.setSourceNode(parent1);
        childEdge1.setTargetNode(child1);
        childEdge2.setSourceNode(parent2);
        childEdge2.setTargetNode(child2);
        childEdge3.setSourceNode(parent3);
        childEdge3.setTargetNode(child3);

        parentEdge1.setContent(new Child());
        parentEdge2.setContent(new Child());
        parentEdge3.setContent(new Child());
        childEdge1.setContent(new Child());
        childEdge2.setContent(new Child());
        childEdge3.setContent(new Child());

        root.getOutEdges().add(parentEdge1);
        root.getOutEdges().add(parentEdge2);
        root.getOutEdges().add(parentEdge3);
        parent1.getInEdges().add(parentEdge1);
        parent1.getOutEdges().add(childEdge1);
        parent2.getInEdges().add(parentEdge2);
        parent2.getOutEdges().add(childEdge2);
        parent3.getInEdges().add(parentEdge3);
        parent3.getOutEdges().add(childEdge3);
        child1.getInEdges().add(childEdge1);
        child2.getInEdges().add(childEdge2);
        child3.getInEdges().add(childEdge3);

        final Graph graph = new GraphImpl(UUID.randomUUID().toString(), new GraphNodeStoreImpl());
        graph.addNode(root);
        graph.addNode(parent1);
        graph.addNode(parent2);
        graph.addNode(parent3);
        graph.addNode(child1);
        graph.addNode(child2);
        graph.addNode(child3);

        final Shape rootShape = mock(Shape.class);
        final Shape parentShape1 = mock(Shape.class);
        final Shape parentShape2 = mock(Shape.class);
        final Shape parentShape3 = mock(Shape.class);
        final Shape childShape1 = mock(Shape.class);
        final Shape childShape2 = mock(Shape.class);
        final Shape childShape3 = mock(Shape.class);

        final Canvas canvas = mock(Canvas.class);
        when(canvas.getShape(eq(root.getUUID()))).thenReturn(rootShape);
        when(canvas.getShape(eq(parent1.getUUID()))).thenReturn(parentShape1);
        when(canvas.getShape(eq(parent2.getUUID()))).thenReturn(parentShape2);
        when(canvas.getShape(eq(parent3.getUUID()))).thenReturn(parentShape3);
        when(canvas.getShape(eq(child1.getUUID()))).thenReturn(childShape1);
        when(canvas.getShape(eq(child2.getUUID()))).thenReturn(childShape2);
        when(canvas.getShape(eq(child3.getUUID()))).thenReturn(childShape3);

        final Diagram diagram = mock(Diagram.class);
        when(diagram.getGraph()).thenReturn(graph);

        final CanvasHandler handler = mock(CanvasHandler.class);
        when(handler.getDiagram()).thenReturn(diagram);
        when(handler.getCanvas()).thenReturn(canvas);

        final List<Shape> shapes = new LinkedList<>();
        shapes.add(rootShape);
        shapes.add(parentShape1);
        shapes.add(childShape1);
        shapes.add(parentShape2);
        shapes.add(childShape2);
        shapes.add(parentShape3);
        shapes.add(childShape3);

        final CaseManagementCanvasHighlightVisitor visitor = new CaseManagementCanvasHighlightVisitor();
        visitor.run(handler, () -> {});

        assertThat(visitor.getShapes(), is(shapes));
    }
}