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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.List;
import java.util.Map;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.ArtifactImpl;
import org.eclipse.bpmn2.impl.FlowElementImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessesTest {

    private static final String ELEMENT_ID = "ELEMENT_ID";

    @Mock
    private PropertyWriter propertyWriter;

    @Mock
    private BoundaryEventPropertyWriter boundaryEventPropertyWriter;

    @Mock
    private List<RootElement> propertyWriterRootElements;

    @Mock
    private List<ItemDefinition> propertyWriterItemDefinitions;

    @Mock
    private ElementParameters propertyWriterElementParameters;

    @Mock
    private FlowElement flowElement;

    @Mock
    private Artifact artifact;

    @Mock
    private Process process;

    @Mock
    private SubProcess subProcess;

    @Mock
    private List<FlowElement> flowElements;

    @Mock
    private List<Artifact> artifacts;

    @Mock
    private Map<String, BasePropertyWriter> childElements;

    @Mock
    private List<RootElement> rootElements;

    @Mock
    private List<ElementParameters> simulationParameters;

    @Mock
    private List<ItemDefinition> itemDefinitions;

    @Before
    public void setUp() {
        when(propertyWriter.getRootElements()).thenReturn(propertyWriterRootElements);
        when(propertyWriter.getSimulationParameters()).thenReturn(propertyWriterElementParameters);
        when(propertyWriter.getItemDefinitions()).thenReturn(propertyWriterItemDefinitions);

        when(boundaryEventPropertyWriter.getRootElements()).thenReturn(propertyWriterRootElements);
        when(boundaryEventPropertyWriter.getSimulationParameters()).thenReturn(propertyWriterElementParameters);
        when(boundaryEventPropertyWriter.getItemDefinitions()).thenReturn(propertyWriterItemDefinitions);

        flowElement = new FlowElementImpl() {};
        flowElement.setId(ELEMENT_ID);
        artifact = new ArtifactImpl() {};
        artifact.setId(ELEMENT_ID);

        when(process.getFlowElements()).thenReturn(flowElements);
        when(process.getArtifacts()).thenReturn(artifacts);
        when(subProcess.getFlowElements()).thenReturn(flowElements);
        when(subProcess.getArtifacts()).thenReturn(artifacts);
    }

    @Test
    public void testAddFlowElementChildOnProcess() {
        testAddFlowElementCase(process);
    }

    @Test
    public void testAddFlowElementChildOnSubProcess() {
        testAddFlowElementCase(subProcess);
    }

    private void testAddFlowElementCase(FlowElementsContainer container) {
        testAddElementChild(propertyWriter, container, flowElement);
        verify(flowElements).add(0, flowElement);
    }

    @Test
    public void testAddBoundaryEventFlowElementOnProcess() {
        testAddBoundaryEventFlowElementCase(process);
    }

    @Test
    public void testAddBoundaryEventFlowElementSubProcess() {
        testAddBoundaryEventFlowElementCase(subProcess);
    }

    private void testAddBoundaryEventFlowElementCase(FlowElementsContainer container) {
        testAddElementChild(boundaryEventPropertyWriter, container, flowElement);
        verify(flowElements).add(flowElement);
    }

    @Test
    public void testAddArtifactElementOnProcess() {
        testAddArtifactCase(process);
    }

    @Test
    public void testAddArtifactElementOnSubProcess() {
        testAddArtifactCase(subProcess);
    }

    private void testAddArtifactCase(FlowElementsContainer container) {
        testAddElementChild(propertyWriter, container, artifact);
        verify(artifacts).add(artifact);
    }

    private void testAddElementChild(BasePropertyWriter propertyWriter, FlowElementsContainer flowElementsContainer, BaseElement baseElement) {
        when(propertyWriter.getElement()).thenReturn(baseElement);
        Processes.addChildElement(propertyWriter, childElements, flowElementsContainer, simulationParameters, itemDefinitions, rootElements);

        verify(childElements).put(ELEMENT_ID, propertyWriter);
        verify(simulationParameters).add(propertyWriterElementParameters);
        verify(rootElements).addAll(propertyWriterRootElements);
        verify(itemDefinitions).addAll(propertyWriterItemDefinitions);
    }

}
