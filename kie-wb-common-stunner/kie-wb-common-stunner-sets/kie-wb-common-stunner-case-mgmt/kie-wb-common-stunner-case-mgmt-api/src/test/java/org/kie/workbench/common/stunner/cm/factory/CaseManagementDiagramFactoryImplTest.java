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

package org.kie.workbench.common.stunner.cm.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDiagramFactoryImplTest {

    private static final String NAME = "diagram1";

    @Mock
    private Graph graph;

    @Mock
    private Metadata metadata;

    @Mock
    private BPMNDiagramFactory bpmnDiagramFactory;

    private CaseManagementDiagramFactoryImpl factory;

    @Before
    public void setup() {
        factory = new CaseManagementDiagramFactoryImpl(bpmnDiagramFactory);
    }

    @Test
    public void assertSetDiagramType() {
        factory.init();
        verify(bpmnDiagramFactory,
               times(1)).setDiagramType(eq(CaseManagementDiagram.class));
    }

    @Test
    public void assertDefSetType() {
        assertEquals(CaseManagementDefinitionSet.class,
                     factory.getDefinitionSetType());
    }

    @Test
    public void assertMetadataType() {
        assertEquals(Metadata.class,
                     factory.getMetadataType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        factory.init();
        factory.build(NAME,
                      metadata,
                      graph);
        verify(bpmnDiagramFactory,
               times(1)).build(eq(NAME),
                               eq(metadata),
                               eq(graph));
    }
}
