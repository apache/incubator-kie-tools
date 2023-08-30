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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.gateways;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.GatewayPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.newNode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GatewayConverterTest {

    private static final String UUID = "UUID";
    private static final String DEFAULT_ROUTE = "DEFAULT_ROUTE";
    private static final String NAME = "NAME";
    private static final String DOCUMENTATION = "DOCUMENTATION";

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    @Mock
    private GatewayPropertyWriter gatewayPropertyWriter;

    private GatewayConverter converter;

    @Before
    public void setUp() {
        converter = new GatewayConverter(propertyWriterFactory);
    }

    @Test
    public void testInclusive() {
        InclusiveGateway gateway = new InclusiveGateway();
        gateway.getGeneral().getName().setValue(NAME);
        gateway.getGeneral().getDocumentation().setValue(DOCUMENTATION);
        gateway.getExecutionSet().getDefaultRoute().setValue(DEFAULT_ROUTE);

        Node<View<BaseGateway>, ?> node = newNode(UUID, gateway);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.InclusiveGateway.class))).thenReturn(gatewayPropertyWriter);

        assertEquals(gatewayPropertyWriter, converter.toFlowElement(node));
        verifyCommonValues(gatewayPropertyWriter, node);
        verify(gatewayPropertyWriter).setDefaultRoute(DEFAULT_ROUTE);
        verify(gatewayPropertyWriter).setGatewayDirection(node);
    }

    @Test
    public void testExclusive() {
        ExclusiveGateway gateway = new ExclusiveGateway();
        gateway.getGeneral().getName().setValue(NAME);
        gateway.getGeneral().getDocumentation().setValue(DOCUMENTATION);
        gateway.getExecutionSet().getDefaultRoute().setValue(DEFAULT_ROUTE);

        Node<View<BaseGateway>, ?> node = newNode(UUID, gateway);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.ExclusiveGateway.class))).thenReturn(gatewayPropertyWriter);

        assertEquals(gatewayPropertyWriter, converter.toFlowElement(node));
        verifyCommonValues(gatewayPropertyWriter, node);
        verify(gatewayPropertyWriter).setDefaultRoute(DEFAULT_ROUTE);
        verify(gatewayPropertyWriter).setGatewayDirection(node);
    }

    @Test
    public void testParallel() {
        ParallelGateway gateway = new ParallelGateway();
        gateway.getGeneral().getName().setValue(NAME);
        gateway.getGeneral().getDocumentation().setValue(DOCUMENTATION);

        Node<View<BaseGateway>, ?> node = newNode(UUID, gateway);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.ParallelGateway.class))).thenReturn(gatewayPropertyWriter);

        assertEquals(gatewayPropertyWriter, converter.toFlowElement(node));
        verifyCommonValues(gatewayPropertyWriter, node);
        verify(gatewayPropertyWriter).setGatewayDirection(node);
    }

    @Test
    public void testEvent() {
        EventGateway gateway = new EventGateway();
        gateway.getGeneral().getName().setValue(NAME);
        gateway.getGeneral().getDocumentation().setValue(DOCUMENTATION);

        Node<View<BaseGateway>, ?> node = newNode(UUID, gateway);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.EventBasedGateway.class))).thenReturn(gatewayPropertyWriter);

        assertEquals(gatewayPropertyWriter, converter.toFlowElement(node));
        verifyCommonValues(gatewayPropertyWriter, node);
    }

    @SuppressWarnings("unchecked")
    private void verifyCommonValues(PropertyWriter propertyWriter, Node node) {
        verify(propertyWriter).setId(UUID);
        verify(propertyWriter).setName(NAME);
        verify(propertyWriter).setDocumentation(DOCUMENTATION);
        verify(propertyWriter).setAbsoluteBounds(node);
    }
}
