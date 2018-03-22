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
package org.kie.workbench.common.stunner.bpmn.project.factory.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNProjectDiagramFactoryTest {

    private static final String NAME = "name1";
    private static final String DIAGRAM_NODE_UUID = "dnuuid";
    private static final org.guvnor.common.services.project.model.Package PKG =
            new org.guvnor.common.services.project.model.Package(mock(Path.class),
                                                                 mock(Path.class),
                                                                 mock(Path.class),
                                                                 mock(Path.class),
                                                                 mock(Path.class),
                                                                 "packageName",
                                                                 "packageCaption",
                                                                 "");

    @Mock
    private ProjectMetadata metadata;
    @Mock
    private Graph graph;
    @Mock
    private Node diagramNode;
    @Mock
    private Bounds bounds;
    private BPMNDiagramImpl diagram;
    private final List<Node> graphNodes = new ArrayList<>(1);

    private BPMNProjectDiagramFactoryImpl tested;

    @Before
    public void setup() {
        diagram = new BPMNDiagramImpl.BPMNDiagramBuilder().build();
        View<BPMNDiagram> diagramNodeContent = new ViewImpl<>(diagram,
                                                                         bounds);
        graphNodes.add(diagramNode);
        when(diagramNode.getUUID()).thenReturn(DIAGRAM_NODE_UUID);
        when(diagramNode.getContent()).thenReturn(diagramNodeContent);
        when(graph.nodes()).thenReturn(graphNodes);
        tested = new BPMNProjectDiagramFactoryImpl();
    }

    @Test
    public void testMetadataType() {
        Class<? extends Metadata> type = tested.getMetadataType();
        assertEquals(ProjectMetadata.class,
                     type);
    }

    @Test
    public void testDefinitionSetType() {
        Class<?> type = tested.getDefinitionSetType();
        assertEquals(BPMNDefinitionSet.class,
                     type);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildNoPackageSpecified() {
        when(metadata.getProjectPackage()).thenReturn(null);
        ProjectDiagram pdiagram = tested.build(NAME, metadata, graph);
        assertNotNull(pdiagram);
        assertEquals(graph,
                     pdiagram.getGraph());
        assertEquals(NAME,
                     diagram.getDiagramSet().getId().getValue());
        assertEquals(Package.DEFAULT_PACKAGE,
                     diagram.getDiagramSet().getPackageProperty().getValue());
        verify(metadata,
               times(1)).setCanvasRootUUID(eq(DIAGRAM_NODE_UUID));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        final String pName = "p1";
        when(metadata.getProjectPackage()).thenReturn(PKG);
        when(metadata.getModuleName()).thenReturn(pName);
        ProjectDiagram pdiagram = tested.build(NAME, metadata, graph);

        assertNotNull(pdiagram);
        assertEquals(graph,
                     pdiagram.getGraph());
        assertEquals(pName + "." + NAME,
                     diagram.getDiagramSet().getId().getValue());
        assertEquals("packageName",
                     diagram.getDiagramSet().getPackageProperty().getValue());
        verify(metadata,
               times(1)).setCanvasRootUUID(eq(DIAGRAM_NODE_UUID));
    }
}
