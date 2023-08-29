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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.gateways;

import org.eclipse.bpmn2.Gateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.GatewayPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.newNode;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GatewayConverterTest {

    private static final String UUID = "UUID";
    private static final String NAME = "NAME";
    private static final String DOCUMENTATION = "DOCUMENTATION";
    private static final String DEFAULT_ROUTE = "DEFAULT_ROUTE";

    @Mock
    private TypedFactoryManager factoryManager;

    @Mock
    private PropertyReaderFactory readerFactory;

    @Mock
    private GatewayPropertyReader propertyReader;

    @Mock
    private CircleDimensionSet circleDimensionSet;

    @Mock
    private FontSet fontSet;

    @Mock
    private BackgroundSet backgroundSet;

    @Mock
    private Bounds bounds;

    private GatewayConverter converter;

    @Before
    public void setUp() {
        when(propertyReader.getName()).thenReturn(NAME);
        when(propertyReader.getDocumentation()).thenReturn(DOCUMENTATION);
        when(propertyReader.getDefaultRoute()).thenReturn(DEFAULT_ROUTE);
        when(propertyReader.getBounds()).thenReturn(bounds);
        when(propertyReader.getCircleDimensionSet()).thenReturn(circleDimensionSet);
        when(propertyReader.getFontSet()).thenReturn(fontSet);
        when(propertyReader.getBackgroundSet()).thenReturn(backgroundSet);
        converter = new GatewayConverter(factoryManager, readerFactory, MarshallingRequest.Mode.AUTO);
    }

    @Test
    public void testConvertInclusiveGateway() {
        InclusiveGateway result = testConvertGateway(new InclusiveGateway(), bpmn2.createInclusiveGateway());
        assertEquals(DEFAULT_ROUTE, result.getExecutionSet().getDefaultRoute().getValue());
    }

    @Test
    public void testConvertExclusiveGateway() {
        ExclusiveGateway result = testConvertGateway(new ExclusiveGateway(), bpmn2.createExclusiveGateway());
        assertEquals(DEFAULT_ROUTE, result.getExecutionSet().getDefaultRoute().getValue());
    }

    @Test
    public void testConvertParallelGateway() {
        testConvertGateway(new ParallelGateway(), bpmn2.createParallelGateway());
    }

    @Test
    public void testConvertEventGateway() {
        testConvertGateway(new EventGateway(), bpmn2.createEventBasedGateway());
    }

    @SuppressWarnings("unchecked")
    private <D extends BaseGateway, G extends Gateway> D testConvertGateway(D gatewayDefinition, G eclipseGateway) {
        when(readerFactory.of(any(eclipseGateway.getClass()))).thenReturn(propertyReader);
        Node<View<D>, Edge> node = (Node<View<D>, Edge>) newNode(UUID, gatewayDefinition);
        when(factoryManager.newNode(UUID, (Class<D>) gatewayDefinition.getClass())).thenReturn(node);

        eclipseGateway.setId(UUID);
        BpmnNode bpmnNode = converter.convert(eclipseGateway).value();
        assertEquals(UUID, bpmnNode.value().getUUID());
        D result = (D) bpmnNode.value().getContent().getDefinition();
        assertCommonValues(result, node);
        return node.getContent().getDefinition();
    }

    private void assertCommonValues(BaseGateway baseGateway, Node<? extends View, Edge> node) {
        assertEquals(NAME, baseGateway.getGeneral().getName().getValue());
        assertEquals(DOCUMENTATION, baseGateway.getGeneral().getDocumentation().getValue());
        assertEquals(backgroundSet, baseGateway.getBackgroundSet());
        assertEquals(fontSet, baseGateway.getFontSet());
        assertEquals(circleDimensionSet, baseGateway.getDimensionsSet());
        assertEquals(bounds, node.getContent().getBounds());
    }
}
