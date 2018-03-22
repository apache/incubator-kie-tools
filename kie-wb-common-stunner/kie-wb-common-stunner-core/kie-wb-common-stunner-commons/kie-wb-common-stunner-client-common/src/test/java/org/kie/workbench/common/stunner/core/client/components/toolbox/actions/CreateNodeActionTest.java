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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCommand;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CreateNodeActionTest {

    private static final String NODE_UUID = "node1";
    private static final String EDGE_UUID = "edge1";
    private static final String TARGET_NODE_UUID = "targetNode1";
    private static final String EDGE_ID = "edgeId1";
    private static final String TARGET_NODE_ID = "targetNodeId1";
    private static final String SSID_UUID = "ss1";
    private static final String ROOT_UUID = "root1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientFactoryManager clientFactoryManager;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private CanvasLayoutUtils canvasLayoutUtils;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasElementSelectedEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Layer layer;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private Node<View<?>, Edge> element;

    @Mock
    private View elementContent;

    @Mock
    private Edge<ViewConnector<?>, Node> edge;

    @Mock
    private Node<View<?>, Edge> targetNode;

    @Mock
    private View targetNodeContent;

    @Mock
    private Index<Node<View<?>, Edge>, Edge<ViewConnector<?>, Node>> graphIndex;

    private CreateNodeAction tested;
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getShapeFactory(eq(SSID_UUID))).thenReturn(shapeFactory);
        when(canvas.getLayer()).thenReturn(layer);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SSID_UUID);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(graphIndex.get(eq(NODE_UUID))).thenReturn(element);
        when(graphIndex.getNode(eq(NODE_UUID))).thenReturn(element);
        when(graphIndex.get(eq(EDGE_UUID))).thenReturn(edge);
        when(graphIndex.getEdge(eq(EDGE_UUID))).thenReturn(edge);
        when(graphIndex.get(eq(TARGET_NODE_UUID))).thenReturn(targetNode);
        when(graphIndex.getNode(eq(TARGET_NODE_UUID))).thenReturn(targetNode);
        when(element.getUUID()).thenReturn(NODE_UUID);
        when(element.getContent()).thenReturn(elementContent);
        when(element.asNode()).thenReturn(element);
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(edge.asEdge()).thenReturn(edge);
        when(targetNode.getUUID()).thenReturn(TARGET_NODE_UUID);
        when(targetNode.asNode()).thenReturn(targetNode);
        when(targetNode.getContent()).thenReturn(targetNodeContent);
        when(sessionCommandManager.execute(eq(canvasHandler), any(Command.class)))
                .thenReturn(CanvasCommandResultBuilder.SUCCESS);
        when(elementContent.getBounds())
                .thenReturn(new BoundsImpl(new BoundImpl(0d,
                                                         0d),
                                           new BoundImpl(100d,
                                                         100d)));
        when(targetNodeContent.getBounds())
                .thenReturn(new BoundsImpl(new BoundImpl(0d,
                                                         0d),
                                           new BoundImpl(100d,
                                                         100d)));
        when(clientFactoryManager.newElement(anyString(),
                                             eq(EDGE_ID)))
                .thenReturn((Element) edge);
        when(clientFactoryManager.newElement(anyString(),
                                             eq(TARGET_NODE_ID)))
                .thenReturn((Element) targetNode);
        this.canvasCommandFactory = new DefaultCanvasCommandFactory(null,
                                                                    null);
        this.tested = new CreateNodeAction(definitionUtils,
                                           clientFactoryManager,
                                           canvasLayoutUtils,
                                           canvasElementSelectedEvent,
                                           translationService,
                                           sessionCommandManager,
                                           canvasCommandFactory)
                .setEdgeId(EDGE_ID)
                .setNodeId(TARGET_NODE_ID);
    }

    @Test
    public void testTitle() {
        assertEquals(TARGET_NODE_ID,
                     tested.getTitleDefinitionId(canvasHandler,
                                                 NODE_UUID));
        tested.getTitle(canvasHandler,
                        NODE_UUID);
        verify(translationService,
               times(1)).getValue(eq(CreateNodeAction.KEY_TITLE));
    }

    @Test
    public void testGlyph() {
        assertEquals(TARGET_NODE_ID,
                     tested.getGlyphId(canvasHandler,
                                       NODE_UUID));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAction() {
        when(canvasLayoutUtils.getNext(eq(canvasHandler),
                                       eq(element),
                                       eq(targetNode)))
                .thenReturn(new Point2D(100d,
                                        500d));
        final MouseClickEvent event = mock(MouseClickEvent.class);
        when(event.getX()).thenReturn(100d);
        when(event.getY()).thenReturn(500d);
        ToolboxAction<AbstractCanvasHandler> cascade =
                tested.onMouseClick(canvasHandler,
                                    NODE_UUID,
                                    event);
        assertEquals(tested,
                     cascade);

        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(sessionCommandManager, times(1)).execute(eq(canvasHandler),
                                                        commandArgumentCaptor.capture());
        DeferredCompositeCommand command = (DeferredCompositeCommand) commandArgumentCaptor.getValue();
        DeferredCommand c0 = (DeferredCommand) command.getCommands().get(0);
        DeferredCommand c1 = (DeferredCommand) command.getCommands().get(1);
        DeferredCommand c2 = (DeferredCommand) command.getCommands().get(2);
        DeferredCommand c3 = (DeferredCommand) command.getCommands().get(3);
        AddNodeCommand addNodeCommand = (AddNodeCommand) c0.getCommand();
        UpdateElementPositionCommand updateElementPositionCommand = (UpdateElementPositionCommand) c1.getCommand();
        AddConnectorCommand addConnectorCommand = (AddConnectorCommand) c2.getCommand();
        SetConnectionTargetNodeCommand setTargetNodeCommand = (SetConnectionTargetNodeCommand) c3.getCommand();
        assertEquals(targetNode, addNodeCommand.getCandidate());
        assertEquals("ss1", addNodeCommand.getShapeSetId());
        assertEquals(edge, addConnectorCommand.getCandidate());
        assertEquals(element, addConnectorCommand.getSource());
        assertEquals("ss1", addConnectorCommand.getShapeSetId());
        assertEquals(edge, setTargetNodeCommand.getEdge());
        assertEquals(targetNode, setTargetNodeCommand.getNode());
        assertEquals(targetNode, updateElementPositionCommand.getElement());
        assertEquals(new Point2D(100d, 500d), updateElementPositionCommand.getLocation());
        final ArgumentCaptor<CanvasSelectionEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(canvasElementSelectedEvent,
               times(1)).fire(eventArgumentCaptor.capture());
        final CanvasSelectionEvent eCaptured = eventArgumentCaptor.getValue();
        assertEquals(TARGET_NODE_UUID,
                     eCaptured.getIdentifiers().iterator().next());
    }
}
