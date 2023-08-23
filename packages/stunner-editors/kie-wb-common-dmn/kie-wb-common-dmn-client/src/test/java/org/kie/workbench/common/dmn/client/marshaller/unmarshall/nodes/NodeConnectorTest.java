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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.AUTO_SOURCE_CONNECTION;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.AUTO_TARGET_CONNECTION;
import static org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeConnector.CENTRE_TOLERANCE;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NodeConnectorTest {

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private JSITDMNElement jsiDMNElement;

    @Mock
    private JSITDMNElementReference jsiDMNElementReference;

    @Mock
    private NodeEntry nodeEntry;

    @Mock
    private Node currentNode;

    @Mock
    private Node requiredNode;

    @Captor
    private ArgumentCaptor<ControlPoint[]> controlPointsCaptor;

    private NodeConnector nodeConnector;

    private String connectorTypeId = getDefinitionId(InformationRequirement.class);
    private String associationTypeId = getDefinitionId(Association.class);

    private Map<String, List<NodeEntry>> entriesById = new HashMap<>();

    private List<JSIDMNEdge> edges = new ArrayList<>();

    private String diagramId = "diagramId";

    private boolean isDMNDIPresent = false;

    @Before
    public void setup() {
        nodeConnector = spy(new NodeConnector(factoryManager));
    }

    @Test
    public void testConnectEdgeToNodesWhenDMNDIIsNotPresent() {

        final JSIDMNEdge newEdge = mock(JSIDMNEdge.class);

        when(jsiDMNElementReference.getHref()).thenReturn("#123");
        when(jsiDMNElement.getId()).thenReturn("789");
        when(nodeEntry.getNode()).thenReturn(requiredNode);

        final View<?> view = mock(View.class);
        final Bounds bounds = mock(Bounds.class);

        when(bounds.getHeight()).thenReturn(50d);
        when(bounds.getWidth()).thenReturn(100d);
        when(view.getBounds()).thenReturn(bounds);

        when(requiredNode.getContent()).thenReturn(view);
        doReturn("456").when(nodeConnector).uuid();
        doReturn(newEdge).when(nodeConnector).newEdge(50, 25);
        doNothing().when(nodeConnector).connectWbEdge(any(), any(), any(), any(), any(), any());

        entriesById.put("123", singletonList(nodeEntry));
        isDMNDIPresent = false;

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector).connectWbEdge(eq(connectorTypeId), eq(diagramId), eq(currentNode), eq(requiredNode), eq(newEdge), eq("456"));
    }

    @Test
    public void testConnectEdgeToNodesWhenDMNDIIsPresent() {

        final JSIDMNEdge existingEdge = mock(JSIDMNEdge.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final String id = "789";
        final String contentDefinitionId = "123";
        final List<NodeEntry> list = singletonList(nodeEntry);
        when(jsiDMNElementReference.getHref()).thenReturn("#123");
        when(jsiDMNElement.getId()).thenReturn(id);
        when(existingEdge.getDmnElementRef()).thenReturn(new QName("", id));
        when(definition.getDefinition()).thenReturn(drgElement);
        when(currentNode.getContent()).thenReturn(definition);
        when(drgElement.getContentDefinitionId()).thenReturn(contentDefinitionId);

        doReturn(true).when(nodeConnector).isEdgeConnectedWithNode(eq(existingEdge), eq(currentNode), eq(list));
        doReturn(Optional.of(requiredNode)).when(nodeConnector).getSourceNode(eq(existingEdge), any());
        doNothing().when(nodeConnector).connectWbEdge(any(), any(), any(), any(), any(), any());

        entriesById.put(contentDefinitionId, list);
        edges.add(existingEdge);
        isDMNDIPresent = true;

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector).connectWbEdge(eq(connectorTypeId), eq(diagramId), eq(currentNode), eq(requiredNode), eq(existingEdge), eq("789"));
        verify(nodeConnector).isEdgeConnectedWithNode(eq(existingEdge), eq(currentNode), eq(list));
    }

    @Test
    public void testConnectEdgeToNodesWhenDMNDIIsPresentAndNodeIsNotConnectedWithEdge() {

        final JSIDMNEdge existingEdge = mock(JSIDMNEdge.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final String id = "789";
        final String contentDefinitionId = "123";
        final List<NodeEntry> list = singletonList(nodeEntry);
        when(jsiDMNElementReference.getHref()).thenReturn("#123");
        when(jsiDMNElement.getId()).thenReturn(id);
        when(existingEdge.getDmnElementRef()).thenReturn(new QName("", id));
        when(definition.getDefinition()).thenReturn(drgElement);
        when(currentNode.getContent()).thenReturn(definition);
        when(drgElement.getContentDefinitionId()).thenReturn(contentDefinitionId);

        doReturn(false).when(nodeConnector).isEdgeConnectedWithNode(eq(existingEdge), eq(currentNode), eq(list));
        doReturn(Optional.of(requiredNode)).when(nodeConnector).getSourceNode(eq(existingEdge), any());
        doNothing().when(nodeConnector).connectWbEdge(any(), any(), any(), any(), any(), any());

        entriesById.put(contentDefinitionId, list);
        edges.add(existingEdge);
        isDMNDIPresent = true;

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector, never()).connectWbEdge(eq(connectorTypeId), eq(diagramId), eq(currentNode), eq(requiredNode), eq(existingEdge), eq("789"));
        verify(nodeConnector).isEdgeConnectedWithNode(eq(existingEdge), eq(currentNode), eq(list));
    }

    @Test
    public void testConnectEdgeToNodesWhenDMNDIIsPresentButExistingNodeIsNotPresent() {

        when(jsiDMNElementReference.getHref()).thenReturn("#123");
        when(jsiDMNElement.getId()).thenReturn("789");
        doNothing().when(nodeConnector).connectWbEdge(any(), any(), any(), any(), any(), any());

        entriesById.put("123", singletonList(nodeEntry));
        isDMNDIPresent = true;

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector, never()).connectWbEdge(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testConnectEdgeToNodesWhenNodeEntriesIsEmpty() {

        when(jsiDMNElementReference.getHref()).thenReturn("#123");
        entriesById.put("123", new ArrayList<>());

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector, never()).connectWbEdge(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testConnectEdgeToNodesWhenNodeEntriesIsNull() {

        when(jsiDMNElementReference.getHref()).thenReturn("#456");

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector, never()).connectWbEdge(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testConnectWbEdge() {

        final JSIDMNEdge edge = mock(JSIDMNEdge.class);
        final Element element = mock(Element.class);
        final String id = "123";
        final Edge wbEdge = mock(Edge.class);
        final ViewConnector viewConnector = mock(ViewConnector.class);

        when(factoryManager.newElement("diagramId#123", connectorTypeId)).thenReturn(element);
        when(element.asEdge()).thenReturn(wbEdge);
        when(wbEdge.getContent()).thenReturn(viewConnector);
        doNothing().when(nodeConnector).connectEdge(any(), any(), any());
        doNothing().when(nodeConnector).setConnectionMagnets(any(), any(), any());

        nodeConnector.connectWbEdge(connectorTypeId, diagramId, currentNode, requiredNode, edge, id);

        verify(nodeConnector).connectEdge(wbEdge, requiredNode, currentNode);
        verify(nodeConnector).setConnectionMagnets(wbEdge, viewConnector, edge);
    }

    @Test
    public void testSetConnectionMagnets() {
        Edge edge = mock(Edge.class);
        ViewConnector viewConnector = mock(ViewConnector.class);
        JSIDMNEdge jsidmnEdge = mock(JSIDMNEdge.class);
        Node sourceNode = mock(Node.class);
        Node targetNode = mock(Node.class);
        View<?> view = mock(View.class);
        Bounds bounds = mock(Bounds.class);
        JSIPoint start = mock(JSIPoint.class);
        JSIPoint waypoint = mock(JSIPoint.class);
        JSIPoint end = mock(JSIPoint.class);

        List<JSIPoint> waypoints = new ArrayList<>();
        waypoints.add(start);
        waypoints.add(waypoint);
        waypoints.add(end);

        NodeConnector nodeConnector = mock(NodeConnector.class);
        doCallRealMethod().when(nodeConnector).setConnectionMagnets(eq(edge), eq(viewConnector), eq(jsidmnEdge));
        doCallRealMethod().when(nodeConnector).setConnectionControlPoints(eq(viewConnector), eq(jsidmnEdge));

        when(jsidmnEdge.getWaypoint()).thenReturn(waypoints);
        when(jsidmnEdge.getId()).thenReturn(AUTO_SOURCE_CONNECTION + AUTO_TARGET_CONNECTION);

        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);

        when(sourceNode.getContent()).thenReturn(view);
        when(targetNode.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(bounds);
        when(bounds.getWidth()).thenReturn(0.0);
        when(bounds.getHeight()).thenReturn(0.0);
        when(start.getX()).thenReturn(0.0);
        when(start.getY()).thenReturn(0.0);
        when(waypoint.getX()).thenReturn(1.0);
        when(waypoint.getY()).thenReturn(1.0);
        when(start.getX()).thenReturn(1.0);
        when(start.getY()).thenReturn(1.0);

        nodeConnector.setConnectionMagnets(edge, viewConnector, jsidmnEdge);
        verify(nodeConnector).isSourceAutoConnectionEdge(jsidmnEdge);
        verify(nodeConnector).isTargetAutoConnectionEdge(jsidmnEdge);
        verify(viewConnector).setControlPoints(any());
    }

    @Test
    public void testConnectAssociationWithControlPoints() {
        final NodeEntry source = createNodeEntryWithBounds(5, 5, 10, 10);
        final Node sourceNode = mock(Node.class);
        final View sourceView = mock(View.class);
        final JSITDMNElement sourceElement = mock(JSITDMNElement.class);
        when(source.getNode()).thenReturn(sourceNode);
        when(sourceNode.getContent()).thenReturn(sourceView);
        when(sourceView.getBounds()).thenReturn(new Bounds(new Bound(0.0, 0.0), new Bound(10.0, 10.0)));
        when(source.getDmnElement()).thenReturn(sourceElement);
        when(sourceElement.getId()).thenReturn("source_id");

        final NodeEntry target = createNodeEntryWithBounds(20, 20, 10, 10);
        final Node targetNode = mock(Node.class);
        final View targetView = mock(View.class);
        final JSITDMNElement targetElement = mock(JSITDMNElement.class);
        when(target.getNode()).thenReturn(targetNode);
        when(targetNode.getContent()).thenReturn(targetView);
        when(targetView.getBounds()).thenReturn(new Bounds(new Bound(15.0, 15.0), new Bound(25.0, 25.0)));
        when(target.getDmnElement()).thenReturn(targetElement);
        when(targetElement.getId()).thenReturn("target_id");

        final JSITAssociation jsitAssociation = mock(JSITAssociation.class);
        when(jsitAssociation.getId()).thenReturn("associationId");
        final JSITDMNElementReference sourceRef = mock(JSITDMNElementReference.class);
        when(sourceRef.getHref()).thenReturn("#source_id");
        final JSITDMNElementReference targetRef = mock(JSITDMNElementReference.class);
        when(targetRef.getHref()).thenReturn("#target_id");
        when(jsitAssociation.getSourceRef()).thenReturn(sourceRef);
        when(jsitAssociation.getTargetRef()).thenReturn(targetRef);

        final JSIDMNEdge jsiAssociationDmnEdge = mock(JSIDMNEdge.class);
        final QName associationElementRef = mock(QName.class);
        when(jsiAssociationDmnEdge.getDmnElementRef()).thenReturn(associationElementRef);
        when(associationElementRef.getLocalPart()).thenReturn("associationId");
        doReturn(Arrays.asList(createPoint(5.0, 5.0), createPoint(12.0, 12.0), createPoint(20.0, 20.0))).when(jsiAssociationDmnEdge).getWaypoint();

        final Element associationElement = mock(Element.class);
        final ViewConnector associationConnectorView = mock(ViewConnector.class);
        final Edge associationWbEdge = mock(Edge.class);

        when(factoryManager.newElement("diagramId#associationId", associationTypeId)).thenReturn(associationElement);
        when(associationElement.asEdge()).thenReturn(associationWbEdge);
        when(associationWbEdge.getContent()).thenReturn(associationConnectorView);

        final JSIDMNDiagram jsidmnDiagram = mock(JSIDMNDiagram.class);
        when(jsidmnDiagram.getId()).thenReturn("diagramId");

        nodeConnector.connect(jsidmnDiagram,
                              Collections.singletonList(jsiAssociationDmnEdge),
                              Collections.singletonList(jsitAssociation),
                              Arrays.asList(source, target),
                              true);

        verify(associationConnectorView).setControlPoints(controlPointsCaptor.capture());
        assertEquals(12.0, controlPointsCaptor.getValue()[0].getLocation().getX(), 0.01);
        assertEquals(12.0, controlPointsCaptor.getValue()[0].getLocation().getY(), 0.01);
        assertEquals(1, controlPointsCaptor.getValue().length);
    }

    @Test
    public void testIsSourceAutoConnectionEdge() {
        String edgeId = "edge-name";
        JSIDMNEdge edge = mock(JSIDMNEdge.class);

        when(edge.getId()).thenReturn(edgeId);
        assertFalse(nodeConnector.isSourceAutoConnectionEdge(edge));

        when(edge.getId()).thenReturn(edgeId + AUTO_SOURCE_CONNECTION);
        assertTrue(nodeConnector.isSourceAutoConnectionEdge(edge));
    }

    @Test
    public void testIsTargetAutoConnectionEdge() {
        String edgeId = "edge-name";
        JSIDMNEdge edge = mock(JSIDMNEdge.class);

        when(edge.getId()).thenReturn(edgeId);
        assertFalse(nodeConnector.isTargetAutoConnectionEdge(edge));

        when(edge.getId()).thenReturn(edgeId + AUTO_TARGET_CONNECTION);
        assertTrue(nodeConnector.isTargetAutoConnectionEdge(edge));
    }

    @Test
    public void testIsAutoConnection() {
        String id = "DMNEdge-ID";
        String autoConnectionID = "#AUTO-CONNECTION";

        JSIDMNEdge jsiDMNEdge1 = mock(JSIDMNEdge.class);
        when(jsiDMNEdge1.getId()).thenReturn(id);

        JSIDMNEdge jsiDMNEdge2 = mock(JSIDMNEdge.class);
        when(jsiDMNEdge2.getId()).thenReturn(id + autoConnectionID);

        JSIDMNEdge jsiDMNEdge3 = mock(JSIDMNEdge.class);
        when(jsiDMNEdge3.getId()).thenReturn(null);

        assertFalse(nodeConnector.isAutoConnection(jsiDMNEdge1, autoConnectionID));
        assertTrue(nodeConnector.isAutoConnection(jsiDMNEdge2, autoConnectionID));
        assertFalse(nodeConnector.isAutoConnection(jsiDMNEdge3, autoConnectionID));
    }

    @Test
    public void testIsEdgeConnectedWithNode() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final List nodeEntries = mock(List.class);
        final Node targetNode = mock(Node.class);
        final Node sourceNode = mock(Node.class);

        doReturn(Optional.of(targetNode)).when(nodeConnector).getTargetNode(jsiDMNEdge,
                                                                            nodeEntries);

        doReturn(Optional.of(sourceNode)).when(nodeConnector).getSourceNode(jsiDMNEdge,
                                                                            nodeEntries);

        assertFalse(nodeConnector.isEdgeConnectedWithNode(jsiDMNEdge,
                                                          currentNode,
                                                          nodeEntries));
    }

    @Test
    public void testIsEdgeConnectedWithNode_WhenIsConnectedBySource() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final List nodeEntries = mock(List.class);
        final Node targetNode = mock(Node.class);

        doReturn(Optional.of(targetNode)).when(nodeConnector).getTargetNode(jsiDMNEdge,
                                                                            nodeEntries);

        doReturn(Optional.of(currentNode)).when(nodeConnector).getSourceNode(jsiDMNEdge,
                                                                             nodeEntries);

        assertTrue(nodeConnector.isEdgeConnectedWithNode(jsiDMNEdge,
                                                         currentNode,
                                                         nodeEntries));
    }

    @Test
    public void testIsEdgeConnectedWithNode_WhenIsConnectedByTarget() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final List nodeEntries = mock(List.class);
        final Node sourceNode = mock(Node.class);

        doReturn(Optional.of(currentNode)).when(nodeConnector).getTargetNode(jsiDMNEdge,
                                                                             nodeEntries);

        doReturn(Optional.of(sourceNode)).when(nodeConnector).getSourceNode(jsiDMNEdge,
                                                                            nodeEntries);

        assertTrue(nodeConnector.isEdgeConnectedWithNode(jsiDMNEdge,
                                                         currentNode,
                                                         nodeEntries));
    }

    @Test
    public void testGetTargetNode_WhenThereIsOnlyASingleNode() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final NodeEntry nodeEntry = mock(NodeEntry.class);
        final Node node = mock(Node.class);
        final List nodeEntries = singletonList(nodeEntry);

        when(nodeEntry.getNode()).thenReturn(node);

        Optional<Node> targetNode = nodeConnector.getTargetNode(jsiDMNEdge, nodeEntries);

        assertTrue(targetNode.isPresent());
        assertEquals(targetNode.get(), node);
    }

    @Test
    public void testGetSourceNode_WhenThereIsOnlyASingleNode() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final NodeEntry nodeEntry = mock(NodeEntry.class);
        final Node node = mock(Node.class);
        final List nodeEntries = singletonList(nodeEntry);

        when(nodeEntry.getNode()).thenReturn(node);

        Optional<Node> sourceNode = nodeConnector.getSourceNode(jsiDMNEdge, nodeEntries);

        assertTrue(sourceNode.isPresent());
        assertEquals(sourceNode.get(), node);
    }

    @Test
    public void testGetSourceNode() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final double sourceX = 1.0;
        final double sourceY = 2.0;
        final JSIPoint sourcePoint = createPoint(sourceX, sourceY);
        final JSIPoint targetPoint = createPoint(7, 8);
        final List<JSIPoint> waypoints = Arrays.asList(sourcePoint, targetPoint);
        final List nodeEntries = mock(List.class);
        final Point2D sourcePoint2D = new Point2D(sourceX, sourceY);

        when(jsiDMNEdge.getWaypoint()).thenReturn(waypoints);

        doReturn(Optional.of(mock(Node.class))).when(nodeConnector).getNodeFromPoint(sourcePoint2D, nodeEntries);

        nodeConnector.getSourceNode(jsiDMNEdge, nodeEntries);

        verify(nodeConnector).getNodeFromPoint(sourcePoint2D, nodeEntries);
    }

    @Test
    public void testGetTargetNode() {

        final JSIDMNEdge jsiDMNEdge = mock(JSIDMNEdge.class);
        final double targetX = 7.0;
        final double targetY = 8.0;
        final JSIPoint sourcePoint = createPoint(1, 2);
        final JSIPoint targetPoint = createPoint(targetX, targetY);
        final JSIPoint middlePoint1 = createPoint(2, 3);
        final JSIPoint middlePoint2 = createPoint(4, 5);
        final List<JSIPoint> waypoints = Arrays.asList(sourcePoint, middlePoint1, middlePoint2, targetPoint);
        final List nodeEntries = mock(List.class);
        final Point2D targetPoint2D = new Point2D(targetX, targetY);

        when(jsiDMNEdge.getWaypoint()).thenReturn(waypoints);

        doReturn(Optional.of(mock(Node.class))).when(nodeConnector).getNodeFromPoint(targetPoint2D, nodeEntries);

        nodeConnector.getTargetNode(jsiDMNEdge, nodeEntries);

        verify(nodeConnector).getNodeFromPoint(targetPoint2D, nodeEntries);
    }

    @Test
    public void testIsPointInsideNode() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(10, 320);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertTrue(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WithXUnderTolerance() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(x - CENTRE_TOLERANCE, 320);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertTrue(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WithXOverTolerance() {

        final double height = 100;
        final double width = 50;
        final double x = 70;
        final double y = 300;
        final Point2D point = new Point2D(x + width + CENTRE_TOLERANCE, 320);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertTrue(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WithYUnderTolerance() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(10, y - CENTRE_TOLERANCE);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertTrue(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WithYOverTolerance() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(10, y + height + CENTRE_TOLERANCE);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertTrue(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WhenXIsOverBounds() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(1000, 320);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertFalse(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WhenYIsOverBounds() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(10, 1320);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertFalse(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WhenXAndYAreOverBounds() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(1000, 1320);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertFalse(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WhenXIsUnderBounds() {

        final double width = 50;
        final double height = 100;
        final double x = 10;
        final double y = 300;
        final Point2D point = new Point2D(8.0, 320.0);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertFalse(isInsideNode);
    }

    @Test
    public void testIsPointInsideNode_WhenYIsUnderBounds() {

        final double height = 100;
        final double width = 50;
        final double x = 0;
        final double y = 300;
        final Point2D point = new Point2D(10, 290);
        final NodeEntry nodeEntry = createNodeEntryWithBounds(x, y, width, height);

        final boolean isInsideNode = nodeConnector.isPointInsideNode(nodeEntry, point);

        assertFalse(isInsideNode);
    }

    NodeEntry createNodeEntryWithBounds(final double x,
                                        final double y,
                                        final double width,
                                        final double height) {

        final NodeEntry nodeEntry = mock(NodeEntry.class);
        final JSIDMNShape shape = mock(JSIDMNShape.class);
        final JSIBounds bounds = mock(JSIBounds.class);

        when(bounds.getHeight()).thenReturn(height);
        when(bounds.getWidth()).thenReturn(width);
        when(bounds.getX()).thenReturn(x);
        when(bounds.getY()).thenReturn(y);
        when(nodeEntry.getDmnShape()).thenReturn(shape);
        when(shape.getBounds()).thenReturn(bounds);

        return nodeEntry;
    }

    private JSIPoint createPoint(final double x, final double y) {
        final JSIPoint point = mock(JSIPoint.class);
        when(point.getX()).thenReturn(x);
        when(point.getY()).thenReturn(y);
        return point;
    }
}
