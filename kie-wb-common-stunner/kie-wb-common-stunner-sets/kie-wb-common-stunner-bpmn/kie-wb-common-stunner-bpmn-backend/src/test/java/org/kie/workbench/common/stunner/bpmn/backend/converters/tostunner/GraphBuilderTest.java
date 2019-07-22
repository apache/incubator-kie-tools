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

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNTestDefinitionFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BasePropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.core.backend.StunnerTestingGraphBackendAPI;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphBuilderTest {

    private static final String BPMN_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    private static final String DIAGRAM_UUID = "DIAGRAM_UUID";
    private static final String SUBPROCESS1_ID = "SUBPROCESS1_ID";
    private static final String SUBPROCESS2_ID = "SUBPROCESS2_ID";
    private static final String SUBPROCESS3_ID = "SUBPROCESS3_ID";

    private DefinitionResolver definitionResolver;

    @Mock
    private org.eclipse.bpmn2.Process process;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private BPMNPlane plane;

    @Mock
    private Definitions definitions;

    private Graph<DefinitionSet, Node> graph;

    private GraphBuilder graphBuilder;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        when(diagram.getPlane()).thenReturn(plane);
        List<RootElement> rootElements = Collections.singletonList(process);
        List<BPMNDiagram> diagrams = Collections.singletonList(diagram);
        when(definitions.getId()).thenReturn(DIAGRAM_UUID);
        when(definitions.getRootElements()).thenReturn(rootElements);
        when(definitions.getDiagrams()).thenReturn(diagrams);
        when(definitions.getRelationships()).thenReturn(Collections.emptyList());
        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
        StunnerTestingGraphBackendAPI api = StunnerTestingGraphBackendAPI.build(BPMNDefinitionSet.class,
                                                                                new BPMNTestDefinitionFactory());
        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(api.getFactoryManager());
        Metadata metadata =
                new MetadataImpl.MetadataImplBuilder(
                        BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class)).build();
        Diagram<Graph<DefinitionSet, Node>, Metadata> diagram =
                api.getFactoryManager().newDiagram(
                        definitionResolver.getDefinitions().getId(),
                        BPMN_ID,
                        metadata);
        graph = diagram.getGraph();
        graphBuilder = new GraphBuilder(graph,
                                        api.getDefinitionManager(),
                                        typedFactoryManager,
                                        api.getRuleManager(),
                                        api.commandFactory,
                                        api.commandManager);
    }

    @Test
    public void testBoundsCalculation() {
        //subprocess1
        double subprocess1X = 10;
        double subprocess1Y = 10;
        double subprocess1Width = 100;
        double subprocess1Height = 200;
        EmbeddedSubprocess subprocess1Definition = mock(EmbeddedSubprocess.class);
        Node<? extends View<? extends BPMNViewDefinition>, ?> subprocess1 = mockNode(subprocess1Definition,
                                                                                     subprocess1X,
                                                                                     subprocess1Y,
                                                                                     subprocess1Width,
                                                                                     subprocess1Height);
        when(subprocess1.getUUID()).thenReturn(SUBPROCESS1_ID);
        BpmnNode subprocess1Node = mockBpmnNode(subprocess1);

        //subprocess2
        double subprocess2X = 20;
        double subprocess2Y = 20;
        double subprocess2Width = 70;
        double subprocess2Height = 170;
        EmbeddedSubprocess subprocess2Definition = mock(EmbeddedSubprocess.class);
        //subprocess1 -> subprocess2
        //absolute coordinates in eclipse model
        Node<? extends View<? extends BPMNViewDefinition>, ?> subprocess2 = mockNode(subprocess2Definition,
                                                                                     subprocess1X + subprocess2X,
                                                                                     subprocess1Y + subprocess2Y,
                                                                                     subprocess2Width,
                                                                                     subprocess2Height);
        when(subprocess2.getUUID()).thenReturn(SUBPROCESS2_ID);
        BpmnNode subprocess2Node = mockBpmnNode(subprocess2);

        //subprocess3
        double subprocess3X = 30;
        double subprocess3Y = 30;
        double subprocess3Width = 30;
        double subprocess3Height = 120;
        EmbeddedSubprocess subprocess3Definition = mock(EmbeddedSubprocess.class);
        //subprocess1 -> subprocess2 -> subprocess3
        //absolute coordinates in eclipse model
        Node<? extends View<? extends BPMNViewDefinition>, ?> subprocess3 = mockNode(subprocess3Definition,
                                                                                     subprocess1X + subprocess2X + subprocess3X,
                                                                                     subprocess1Y + subprocess2Y + subprocess3Y,
                                                                                     subprocess3Width, subprocess3Height);
        when(subprocess3.getUUID()).thenReturn(SUBPROCESS3_ID);
        BpmnNode subprocess3Node = mockBpmnNode(subprocess3);

        //subprocess1 -> subprocess2 -> subprocess3
        Node<? extends View<? extends BPMNViewDefinition>, ?> rootDiagram = mockNode(mock(BPMNDiagramImpl.class), 0, 0, 1000, 1000);
        when(rootDiagram.getUUID()).thenReturn(DIAGRAM_UUID);
        BpmnNode rootNode = mockBpmnNode(rootDiagram);

        subprocess1Node.setParent(rootNode);
        subprocess2Node.setParent(subprocess1Node);
        subprocess3Node.setParent(subprocess2Node);
        graphBuilder.buildGraph(rootNode);

        //stunner model must have the relative coordinates
        assertNodePosition(SUBPROCESS1_ID, subprocess1X, subprocess1Y);
        assertNodePosition(SUBPROCESS2_ID, subprocess2X, subprocess2Y);
        assertNodePosition(SUBPROCESS3_ID, subprocess3X, subprocess3Y);
    }

    @SuppressWarnings("unchecked")
    private void assertNodePosition(String uuid, double x, double y) {
        Node<? extends View<? extends BPMNViewDefinition>, ?> node = graph.getNode(uuid);
        assertNotNull(node);
        Bounds bounds = node.getContent().getBounds();
        assertEquals(x, bounds.getUpperLeft().getX(), 0);
        assertEquals(y, bounds.getUpperLeft().getY(), 0);
    }

    public static BpmnNode mockBpmnNode(Node<? extends View<? extends BPMNViewDefinition>, ?> node) {
        Bounds nodeBounds = node.getContent().getBounds();
        BasePropertyReader propertyReader = mock(BasePropertyReader.class);
        BPMNShape shape = mock(BPMNShape.class);
        org.eclipse.dd.dc.Bounds bounds = dc.createBounds();
        bounds.setX(nodeBounds.getUpperLeft().getX().floatValue());
        bounds.setY(nodeBounds.getUpperLeft().getY().floatValue());
        bounds.setWidth(Double.valueOf(nodeBounds.getWidth()).floatValue());
        bounds.setHeight(Double.valueOf(nodeBounds.getHeight()).floatValue());
        Bounds readerBounds = Bounds.create(nodeBounds.getUpperLeft().getX(),
                                            nodeBounds.getUpperLeft().getY(),
                                            nodeBounds.getLowerRight().getX(),
                                            nodeBounds.getLowerRight().getY());
        when(propertyReader.getBounds()).thenReturn(readerBounds);
        when(shape.getBounds()).thenReturn(bounds);
        when(propertyReader.getShape()).thenReturn(shape);
        return BpmnNode.of(node, propertyReader);
    }

    public static Node<? extends View<? extends BPMNViewDefinition>, ?> mockNode(BPMNViewDefinition definition, double x, double y, double width, double height) {
        return mockNode(definition, Bounds.create(x, y, x + width, y + height));
    }

    @SuppressWarnings("unchecked")
    private static Node<? extends View<? extends BPMNViewDefinition>, ?> mockNode(BPMNViewDefinition definition, Bounds bounds) {
        Node<View<BPMNViewDefinition>, ?> node = mock(Node.class);
        View<BPMNViewDefinition> view = new ViewImpl<>(definition, bounds);
        when(node.getContent()).thenReturn(view);
        return node;
    }
}
