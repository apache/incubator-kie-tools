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

package org.kie.workbench.common.stunner.bpmn.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDiagramFactoryImplTest {

    private static final String NAME = "diagram1";
    private static final String DIAGRAM_NODE_UUID = "uuidDiagramNode";

    @Mock
    private Graph graph;

    @Mock
    private Metadata metadata;

    @Mock
    private Node diagramNode;

    private BPMNDiagramFactoryImpl tested;

    @Before
    public void init() {
        when(diagramNode.getUUID()).thenReturn(DIAGRAM_NODE_UUID);
        tested = new BPMNDiagramFactoryImpl();
        tested.diagramProvider = graph -> diagramNode;
    }

    @Test
    public void testTypes() {
        assertEquals(BPMNDefinitionSet.class,
                     tested.getDefinitionSetType());
        assertEquals(Metadata.class,
                     tested.getMetadataType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        final Diagram<Graph, Metadata> diagram = tested.build(NAME,
                                                                       metadata,
                                                                       graph);
        assertNotNull(diagram);
        assertEquals(NAME,
                     diagram.getName());
        assertEquals(metadata,
                     diagram.getMetadata());
        assertEquals(graph,
                     diagram.getGraph());
        verify(metadata,
               times(1)).setCanvasRootUUID(eq(DIAGRAM_NODE_UUID));
    }
}
