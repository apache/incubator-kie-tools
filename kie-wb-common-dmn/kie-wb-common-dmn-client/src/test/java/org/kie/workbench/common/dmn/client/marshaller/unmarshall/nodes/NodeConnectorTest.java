/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.AUTO_SOURCE_CONNECTION;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.AUTO_TARGET_CONNECTION;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
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

    private NodeConnector nodeConnector;

    private String connectorTypeId = getDefinitionId(InformationRequirement.class);

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
        doReturn("456").when(nodeConnector).uuid();
        doReturn(newEdge).when(nodeConnector).newEdge();
        doNothing().when(nodeConnector).connectWbEdge(any(), any(), any(), any(), any(), any());

        entriesById.put("123", singletonList(nodeEntry));
        isDMNDIPresent = false;

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector).connectWbEdge(eq(connectorTypeId), eq(diagramId), eq(currentNode), eq(requiredNode), eq(newEdge), eq("456"));
    }

    @Test
    public void testConnectEdgeToNodesWhenDMNDIIsPresent() {

        final JSIDMNEdge existingEdge = mock(JSIDMNEdge.class);

        when(jsiDMNElementReference.getHref()).thenReturn("#123");
        when(jsiDMNElement.getId()).thenReturn("789");
        when(existingEdge.getDmnElementRef()).thenReturn(new QName("", "789"));
        doReturn(requiredNode).when(nodeConnector).getNode(eq(existingEdge), any());
        doNothing().when(nodeConnector).connectWbEdge(any(), any(), any(), any(), any(), any());

        entriesById.put("123", singletonList(nodeEntry));
        edges.add(existingEdge);
        isDMNDIPresent = true;

        nodeConnector.connectEdgeToNodes(connectorTypeId, jsiDMNElement, jsiDMNElementReference, entriesById, diagramId, edges, isDMNDIPresent, currentNode);

        verify(nodeConnector).connectWbEdge(eq(connectorTypeId), eq(diagramId), eq(currentNode), eq(requiredNode), eq(existingEdge), eq("789"));
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
}
