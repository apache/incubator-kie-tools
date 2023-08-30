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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ElementParameters;
import bpsim.Scenario;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionResolverTest {

    private static final String ID = "PARENT_ID";

    @Mock
    private Definitions definitions;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private BPMNPlane plane;

    @Mock
    private FeatureMap featureMap;

    @Mock
    private Process process;

    @Mock
    private ExtensionAttributeValue extensionAttributeValue;

    @Mock
    private Relationship relationship;

    private DefinitionResolver definitionResolver;
    private EList<DiagramElement> planeElements;
    private List<BPSimDataType> simData = new ArrayList<>();
    private EList<ExtensionAttributeValue> extensionAttributeValues = ECollections.newBasicEList();
    private EList<Relationship> relationships = ECollections.newBasicEList();

    @Before
    public void setUp() {
        planeElements = ECollections.newBasicEList();
        when(definitions.getRootElements()).thenReturn(ECollections.singletonEList(process));
        when(definitions.getDiagrams()).thenReturn(ECollections.singletonEList(diagram));
        when(definitions.getRelationships()).thenReturn(ECollections.emptyEList());
        when(featureMap.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA, true)).thenReturn(simData);
        when(extensionAttributeValue.getValue()).thenReturn(featureMap);
        extensionAttributeValues.add(extensionAttributeValue);
        when(relationship.getExtensionValues()).thenReturn(extensionAttributeValues);
        relationships.add(relationship);
        when(definitions.getRelationships()).thenReturn(relationships);
        when(diagram.getPlane()).thenReturn(plane);
        when(plane.getPlaneElement()).thenReturn(planeElements);
        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
    }

    @Test
    public void testCalculateResolutionFactor() {
        BPMNDiagram diagram = mock(BPMNDiagram.class);
        when(diagram.getResolution()).thenReturn(0f);
        double factor = DefinitionResolver.calculateResolutionFactor(diagram);
        assertEquals(1d, factor, 0d);
        when(diagram.getResolution()).thenReturn(250f);
        factor = DefinitionResolver.calculateResolutionFactor(diagram);
        assertEquals(0.45d, factor, 0d);
    }

    @Test
    public void testGetShape() {
        BPMNShape shape = mock(BPMNShape.class);
        BaseElement bpmnElement = mock(BaseElement.class);
        when(shape.getBpmnElement()).thenReturn(bpmnElement);
        when(bpmnElement.getId()).thenReturn(ID);
        planeElements.add(shape);
        assertEquals(shape, definitionResolver.getShape(ID));
    }

    @Test
    public void testGetEdge() {
        BPMNEdge edge = mock(BPMNEdge.class);
        BaseElement bpmnElement = mock(BaseElement.class);
        when(edge.getBpmnElement()).thenReturn(bpmnElement);
        when(bpmnElement.getId()).thenReturn(ID);
        planeElements.add(edge);
        assertEquals(edge, definitionResolver.getEdge(ID));
    }

    @Test
    public void testSimulation() {
        String elementRef = "some_element_ref";

        EList<Scenario> scenarios = ECollections.newBasicEList();
        Scenario scenario = mock(Scenario.class);
        scenarios.add(scenario);

        EList<ElementParameters> parameters = ECollections.newBasicEList();
        ElementParameters parameter = mock(ElementParameters.class);
        when(parameter.getElementRef()).thenReturn(elementRef);
        parameters.add(parameter);

        when(scenario.getElementParameters()).thenReturn(parameters);

        BPSimDataType simDataType = mock(BPSimDataType.class);
        simData.add(simDataType);
        when(simDataType.getScenario()).thenReturn(scenarios);

        setUp();

        assertEquals(parameter, definitionResolver.resolveSimulationParameters(elementRef).get());
    }

    @Test
    public void testGetDefinitionsId() {
        assertNotNull(definitionResolver.getDefinitionsId());

        String definitionId = "12345";

        when(definitions.getId()).thenReturn(definitionId);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        assertEquals(definitionId, definitionResolver.getDefinitionsId());
    }
}
