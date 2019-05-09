/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionResolverTest {

    private static final String ID = "ID";

    @Mock
    private Definitions definitions;

    private List<BPMNDiagram> diagrams = new ArrayList<>();

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private BPMNPlane plane;

    private List<DiagramElement> planeElement = new ArrayList<>();

    @Mock
    private Process process;

    private List<RootElement> rootElements = new ArrayList<>();

    private DefinitionResolver definitionResolver;

    @Before
    public void setUp() {
        rootElements.add(process);
        when(definitions.getRootElements()).thenReturn(rootElements);
        diagrams.add(diagram);
        when(definitions.getDiagrams()).thenReturn(diagrams);
        when(diagram.getPlane()).thenReturn(plane);
        when(plane.getPlaneElement()).thenReturn(planeElement);
        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
    }

    @Test
    public void testObtainResolutionFactor() {
        double factor = DefinitionResolver.obtainResolutionFactor();
        assertEquals(DefinitionResolver.DEFAULT_RESOLUTION, factor, 0d);
        System.setProperty(DefinitionResolver.BPMN_DIAGRAM_RESOLUTION_PROPERTY, "0.25");
        factor = DefinitionResolver.obtainResolutionFactor();
        assertEquals(0.25d, factor, 0d);
        System.clearProperty(DefinitionResolver.BPMN_DIAGRAM_RESOLUTION_PROPERTY);
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
        planeElement.add(shape);
        assertEquals(shape, definitionResolver.getShape(ID));
    }

    @Test
    public void testGetEdge() {
        BPMNEdge edge = mock(BPMNEdge.class);
        BaseElement bpmnElement = mock(BaseElement.class);
        when(edge.getBpmnElement()).thenReturn(bpmnElement);
        when(bpmnElement.getId()).thenReturn(ID);
        planeElement.add(edge);
        assertEquals(edge, definitionResolver.getEdge(ID));
    }
}
