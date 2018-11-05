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

package org.kie.workbench.common.stunner.bpmn.project.backend.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDelegateGraphFactoryTest {

    public static final String GRAPH_UUID = UUID.uuid();
    public static final String DEFINITION = "def";
    public static final String SOURCE = "source";
    private BPMNDelegateGraphFactory tested;

    @Mock
    private BPMNGraphFactoryImpl bpmnGraphFactory;

    @Mock
    private CaseGraphFactoryImpl caseGraphFactory;

    @Mock
    private ProjectMetadata projectMetadata;

    @Before
    public void setUp() throws Exception {
        when(bpmnGraphFactory.accepts(SOURCE)).thenReturn(true);
        when(caseGraphFactory.accepts(SOURCE)).thenReturn(true);
        tested = spy(new BPMNDelegateGraphFactory(bpmnGraphFactory, caseGraphFactory));
    }

    @Test
    public void setDiagramType() {
        tested.setDiagramType(BPMNDiagramImpl.class);
        verify(bpmnGraphFactory).setDiagramType(BPMNDiagramImpl.class);
        verify(caseGraphFactory).setDiagramType(BPMNDiagramImpl.class);
    }

    @Test
    public void getFactoryType() {
        assertEquals(tested.getFactoryType(), BPMNGraphFactory.class);
    }

    @Test
    public void buildCase() {
        when(projectMetadata.getProjectType()).thenReturn(ProjectType.CASE.name());
        tested.build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(caseGraphFactory).build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(bpmnGraphFactory, never()).build(GRAPH_UUID, DEFINITION, projectMetadata);
    }

    @Test
    public void buildBPMN() {
        when(projectMetadata.getProjectType()).thenReturn(ProjectType.BPMN.name());
        tested.build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(caseGraphFactory, never()).build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(bpmnGraphFactory).build(GRAPH_UUID, DEFINITION, projectMetadata);
    }

    @Test
    public void buildDefault() {
        tested.build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(caseGraphFactory, never()).build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(bpmnGraphFactory).build(GRAPH_UUID, DEFINITION, projectMetadata);

        tested.build(GRAPH_UUID, DEFINITION);
        verify(caseGraphFactory, never()).build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(bpmnGraphFactory).build(GRAPH_UUID, DEFINITION, null);

        final Metadata metadata = mock(Metadata.class);
        tested.build(GRAPH_UUID, DEFINITION, metadata);
        verify(caseGraphFactory, never()).build(GRAPH_UUID, DEFINITION, projectMetadata);
        verify(bpmnGraphFactory).build(GRAPH_UUID, DEFINITION, metadata);
    }

    @Test
    public void build() {
        tested.build(GRAPH_UUID, DEFINITION);
        verify(tested).build(GRAPH_UUID, DEFINITION, null);
    }

    @Test
    public void accepts() {
        assertTrue(tested.accepts(SOURCE));
        verify(bpmnGraphFactory).accepts(SOURCE);
        verify(caseGraphFactory).accepts(SOURCE);
    }
}