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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GatewayDirection;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: Kogito
@Ignore
public class GatewayPropertyWriterTest extends AbstractBasePropertyWriterTest<GatewayPropertyWriter, Gateway> {

    private static final String SEQUENCE_ID = "SEQUENCE_ID";

    @Mock
    private FeatureMap featureMap;

    @Captor
    private ArgumentCaptor<FeatureMap.Entry> entryCaptor;

    @Mock
    private PropertyWriter anotherPropertyWriter;

    @Mock
    private SequenceFlow sequenceFlow;

    @Override
    protected GatewayPropertyWriter newPropertyWriter(Gateway baseElement, VariableScope variableScope) {
        return new GatewayPropertyWriter(baseElement, variableScope);
    }

    @Override
    protected Gateway mockElement() {
        return mockGateway(Gateway.class, ID);
    }

    @Test
    public void testSetDefaultRoute() {
        testSetDefaultRoute("defaultRoute", "defaultRoute");
    }

    @Test
    public void testSetDefaultRouteWithPrefix() {
        testSetDefaultRoute("prefix : defaultRoute", "defaultRoute");
    }

    @Test
    public void testSetDefaultRouteNull() {
        propertyWriter.setDefaultRoute(null);
        verify(featureMap, never()).add(any());
    }

    private void testSetDefaultRoute(String defaultRoute, String expectedGatewayId) {
        propertyWriter.setDefaultRoute(defaultRoute);
        verify(featureMap).add(entryCaptor.capture());
        assertEquals(String.format("drools:dg=%s", defaultRoute), entryCaptor.getValue().toString());
        assertEquals(expectedGatewayId, propertyWriter.getDefaultGatewayId());
    }

    @Test
    public void testSetSourceForExclusiveGateway() {
        ExclusiveGateway exclusiveGateway = mockGateway(ExclusiveGateway.class, ID);
        prepareTestSetSourceOrTarget(exclusiveGateway);
        propertyWriter.setSource(anotherPropertyWriter);
        verify(exclusiveGateway).setDefault(sequenceFlow);
    }

    @Test
    public void testSetSourceForInclusiveGateway() {
        InclusiveGateway inclusiveGateway = mockGateway(InclusiveGateway.class, ID);
        prepareTestSetSourceOrTarget(inclusiveGateway);
        propertyWriter.setSource(anotherPropertyWriter);
        verify(inclusiveGateway).setDefault(sequenceFlow);
    }

    @Test
    public void testSetTargetForExclusiveGateway() {
        ExclusiveGateway exclusiveGateway = mockGateway(ExclusiveGateway.class, ID);
        prepareTestSetSourceOrTarget(exclusiveGateway);
        propertyWriter.setTarget(anotherPropertyWriter);
        verify(exclusiveGateway).setDefault(sequenceFlow);
    }

    @Test
    public void testSetTargetForInclusiveGateway() {
        InclusiveGateway inclusiveGateway = mockGateway(InclusiveGateway.class, ID);
        prepareTestSetSourceOrTarget(inclusiveGateway);
        propertyWriter.setTarget(anotherPropertyWriter);
        verify(inclusiveGateway).setDefault(sequenceFlow);
    }

    private void prepareTestSetSourceOrTarget(Gateway gateway) {
        when(sequenceFlow.getId()).thenReturn(SEQUENCE_ID);
        when(anotherPropertyWriter.getElement()).thenReturn(sequenceFlow);
        propertyWriter = new GatewayPropertyWriter(gateway, variableScope);
        propertyWriter.setDefaultRoute(SEQUENCE_ID);
    }

    @Test
    public void testSetGatewayDirectionWithDivergingNode() {
        Node node = mockNode(1, 2);
        propertyWriter.setGatewayDirection(node);
        verify(element).setGatewayDirection(GatewayDirection.DIVERGING);
    }

    @Test
    public void testSetGatewayDirectionWithConvergingNode() {
        Node node = mockNode(2, 1);
        propertyWriter.setGatewayDirection(node);
        verify(element).setGatewayDirection(GatewayDirection.CONVERGING);
    }

    @Test
    public void testSetGatewayDirectionWithNotConfiguredNode() {
        Node node = mockNode(0, 0);
        propertyWriter.setGatewayDirection(node);
        verify(element).setGatewayDirection(GatewayDirection.UNSPECIFIED);
    }

    @Test
    public void testSetGatewayDirection() {
        GatewayDirection randomDirection = GatewayDirection.CONVERGING;
        propertyWriter.setGatewayDirection(randomDirection);
        verify(element).setGatewayDirection(randomDirection);
    }

    protected <T extends Gateway> T mockGateway(Class<T> classToMock, String id) {
        T gateway = mock(classToMock);
        when(gateway.getAnyAttribute()).thenReturn(featureMap);
        when(gateway.getId()).thenReturn(id);
        return gateway;
    }

    private Node mockNode(int inEdgesSize, int outEdgesSize) {
        Node node = mock(Node.class);
        List<Edge> inEdges = new ArrayList<>();
        when(node.getInEdges()).thenReturn(inEdges);
        Edge edge;
        for (int i = 0; i < inEdgesSize; i++) {
            edge = mock(Edge.class);
            when(edge.getContent()).thenReturn(mock(ViewConnector.class));
            inEdges.add(edge);
        }
        List<Edge> outEdges = new ArrayList<>();
        when(node.getOutEdges()).thenReturn(outEdges);
        for (int i = 0; i < outEdgesSize; i++) {
            edge = mock(Edge.class);
            when(edge.getContent()).thenReturn(mock(ViewConnector.class));
            outEdges.add(edge);
        }
        return node;
    }
}
