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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: @RunWith(GwtMockitoTestRunner.class)
@Ignore

public class CreateConnectorToolboxActionTest {

    private static final String NODE_UUID = "node1";
    private static final String EDGE_UUID = "edge1";
    private static final String TARGET_NODE_UUID = "targetNode1";
    private static final String EDGE_ID = "edgeId1";
    private static final String SSID_UUID = "ss1";
    private static final String ROOT_UUID = "root1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ClientFactoryManager clientFactoryManager;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private Node<View<?>, Edge> element;

    @Mock
    private Edge<ViewConnector<?>, Node> edge;

    @Mock
    private Node<View<?>, Edge> targetNode;

    private CreateConnectorToolboxAction tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getShapeFactory(eq(SSID_UUID))).thenReturn(shapeFactory);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SSID_UUID);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(element.getUUID()).thenReturn(NODE_UUID);
        when(element.asNode()).thenReturn(element);
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(edge.asEdge()).thenReturn(edge);
        when(targetNode.getUUID()).thenReturn(TARGET_NODE_UUID);
        when(targetNode.asNode()).thenReturn(targetNode);
        when(clientFactoryManager.newElement(anyString(),
                                             eq(EDGE_ID)))
                .thenReturn((Element) edge);

        this.tested = new CreateConnectorToolboxAction(definitionUtils,
                                                       clientFactoryManager,
                                                       translationService,
                                                       // TODO
                                                       null)
                .setEdgeId(EDGE_ID);
    }

    @Test
    public void testTitle() {
        assertEquals(EDGE_ID,
                     tested.getTitleDefinitionId(canvasHandler,
                                                 NODE_UUID));
        tested.getTitle(canvasHandler,
                        NODE_UUID);
        verify(translationService,
               times(1)).getValue(eq(CreateConnectorToolboxAction.KEY_TITLE));
    }

    @Test
    public void testGlyph() {
        assertEquals(EDGE_ID,
                     tested.getGlyphId(canvasHandler,
                                       NODE_UUID));

        tested.getGlyph(canvasHandler,
                        NODE_UUID);

        verify(shapeFactory).getGlyph(EDGE_ID,
                                      AbstractToolboxAction.ToolboxGlyphConsumer.class);
    }
}
