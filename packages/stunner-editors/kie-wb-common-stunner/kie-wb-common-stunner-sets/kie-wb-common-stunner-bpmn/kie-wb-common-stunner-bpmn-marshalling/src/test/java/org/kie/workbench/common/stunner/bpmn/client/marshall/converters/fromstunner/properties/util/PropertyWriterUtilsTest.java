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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertyWriterUtilsTest {

    private static final String SOURCE_SHAPE_ID = "SOURCE_SHAPE_ID";
    private static final String TARGET_SHAPE_ID = "TARGET_SHAPE_ID";

    @Mock
    private BasePropertyWriter sourceWriter;

    @Mock
    private BaseElement sourceElement;

    @Mock
    private BasePropertyWriter targetWriter;

    @Mock
    private BaseElement targetElement;

    @Test
    public void testCreateBPMNEdge() {
        BPMNShape sourceShape = mockShape(SOURCE_SHAPE_ID, 1, 1, 4, 4);
        BPMNShape targetShape = mockShape(TARGET_SHAPE_ID, 10, 10, 4, 4);
        when(sourceWriter.getShape()).thenReturn(sourceShape);
        when(targetWriter.getShape()).thenReturn(targetShape);
        Connection sourceConnection = mockConnection(1, 1);
        Connection targetConnection = mockConnection(10, 10);
        ControlPoint[] controlPoints = new ControlPoint[]{
                new ControlPoint(Point2D.create(3, 3)),
                new ControlPoint(Point2D.create(4, 4)),
                new ControlPoint(Point2D.create(5, 5))
        };

        BPMNEdge edge = PropertyWriterUtils.createBPMNEdge(sourceWriter, targetWriter, sourceConnection, controlPoints, targetConnection);

        assertEquals("edge_SOURCE_SHAPE_ID_to_TARGET_SHAPE_ID", edge.getId());
        assertWaypoint(2, 2, 0, edge.getWaypoint());
        assertWaypoint(3, 3, 1, edge.getWaypoint());
        assertWaypoint(4, 4, 2, edge.getWaypoint());
        assertWaypoint(5, 5, 3, edge.getWaypoint());
        assertWaypoint(20, 20, 4, edge.getWaypoint());
    }

    @Test
    public void testGetDockSourceNodeWhenDocked() {
        Node<? extends View, Edge> dockSourceNode = mock(Node.class);
        Node<? extends View, Edge> node = mockDockedNode(dockSourceNode);
        Optional<Node<View, Edge>> result = PropertyWriterUtils.getDockSourceNode(node);
        assertTrue(result.isPresent());
        assertEquals(dockSourceNode, result.get());
    }

    @Test
    public void testGetDockSourceNodeWhenNotDocked() {
        Node<? extends View, Edge> node = mock(Node.class);
        List<Edge> edges = new ArrayList<>();
        when(node.getInEdges()).thenReturn(edges);
        Optional<Node<View, Edge>> result = PropertyWriterUtils.getDockSourceNode(node);
        assertFalse(result.isPresent());
    }

    public static void assertWaypoint(float x, float y, int index, List<Point> waypoints) {
        assertEquals(x, waypoints.get(index).getX(), 0);
        assertEquals(y, waypoints.get(index).getY(), 0);
    }

    public static BPMNShape mockShape(String id, float x, float y, float width, float height) {
        BPMNShape shape = mock(BPMNShape.class);
        when(shape.getId()).thenReturn(id);
        Bounds bounds = mock(Bounds.class);
        when(bounds.getX()).thenReturn(x);
        when(bounds.getY()).thenReturn(y);
        when(shape.getBounds()).thenReturn(bounds);
        return shape;
    }

    public static Connection mockConnection(double x, double y) {
        Connection connection = mock(Connection.class);
        Point2D locationPoint = Point2D.create(x, y);
        when(connection.getLocation()).thenReturn(locationPoint);
        return connection;
    }

    @SuppressWarnings("unchecked")
    public static ViewConnector<? extends BPMNViewDefinition> mockConnector(double sourceX, double sourceY, double targetX, double targetY, ControlPoint[] controlPoints) {
        ViewConnector<? extends BPMNViewDefinition> connector = mock(ViewConnector.class);
        Connection sourceConnection = mockConnection(sourceX, sourceY);
        Connection targetConnection = mockConnection(targetX, targetY);

        Optional<Connection> sourceConnectionOpt = Optional.of(sourceConnection);
        Optional<Connection> targetConnectionOpt = Optional.of(targetConnection);

        when(connector.getSourceConnection()).thenReturn(sourceConnectionOpt);
        when(connector.getTargetConnection()).thenReturn(targetConnectionOpt);
        when(connector.getControlPoints()).thenReturn(controlPoints);
        return connector;
    }

    private static Node<? extends View, Edge> mockDockedNode(Node dockSourceNode) {
        Dock dockContent = mock(Dock.class);
        Edge edge = mock(Edge.class);
        List<Edge> inEdges = Collections.singletonList(edge);
        when(edge.getContent()).thenReturn(dockContent);
        when(edge.getSourceNode()).thenReturn(dockSourceNode);
        Node<? extends View, Edge> node = mock(Node.class);
        when(node.getInEdges()).thenReturn(inEdges);
        return node;
    }
}
