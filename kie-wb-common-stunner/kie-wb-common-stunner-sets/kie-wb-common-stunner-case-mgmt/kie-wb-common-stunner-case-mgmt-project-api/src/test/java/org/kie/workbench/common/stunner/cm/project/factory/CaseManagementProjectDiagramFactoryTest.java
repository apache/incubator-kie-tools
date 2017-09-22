/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.project.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.factory.impl.BPMNProjectDiagramFactory;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementProjectDiagramFactoryTest {

    @Mock
    private Graph graph;

    @Mock
    private ProjectMetadata metadata;

    @Mock
    private BPMNProjectDiagramFactory bpmnDiagramFactory;

    private CaseManagementProjectDiagramFactory factory;

    @Before
    public void setup() {
        this.factory = new CaseManagementProjectDiagramFactory(bpmnDiagramFactory);
    }

    @Test
    public void assertDiagamType() {
        factory.init();
        verify(bpmnDiagramFactory,
               times(1)).setDiagramType(eq(CaseManagementDiagram.class));
    }

    @Test
    public void assertDefinitionSetType() {
        assertEquals(CaseManagementDefinitionSet.class,
                     factory.getDefinitionSetType());
    }

    @Test
    public void assertMetadataType() {
        assertEquals(ProjectMetadata.class,
                     factory.getMetadataType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        factory.init();
        factory.build("diagram1",
                      metadata,
                      graph);
        verify(bpmnDiagramFactory,
               times(1)).build(eq("diagram1"),
                               eq(metadata),
                               eq(graph));
    }
}
