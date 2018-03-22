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
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.EdgeBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.event.CancelCanvasAction;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CreateConnectorActionTest {

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
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientFactoryManager clientFactoryManager;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    private ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory;

    @Mock
    private EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl;

    @Mock
    private CanvasHighlight canvasHighlight;

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
    private Edge<ViewConnector<?>, Node> edge;

    @Mock
    private Node<View<?>, Edge> targetNode;

    @Mock
    private Index<Node<View<?>, Edge>, Edge<ViewConnector<?>, Node>> graphIndex;

    @Mock
    private CancelCanvasAction cancelCanvasAction;

    @Mock
    private DragProxy<AbstractCanvasHandler, ConnectorDragProxy.Item, DragProxyCallback> dragProxy;

    private CreateConnectorAction tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getShapeFactory(eq(SSID_UUID))).thenReturn(shapeFactory);
        when(connectorDragProxyFactory.proxyFor(any(AbstractCanvasHandler.class)))
                .thenReturn(connectorDragProxyFactory);
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
        when(element.asNode()).thenReturn(element);
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(edge.asEdge()).thenReturn(edge);
        when(targetNode.getUUID()).thenReturn(TARGET_NODE_UUID);
        when(targetNode.asNode()).thenReturn(targetNode);
        when(clientFactoryManager.newElement(anyString(),
                                             eq(EDGE_ID)))
                .thenReturn((Element) edge);
        when(connectorDragProxyFactory.show(any(ConnectorDragProxy.Item.class),
                                            eq(100),
                                            eq(500),
                                            any(DragProxyCallback.class))).thenReturn(dragProxy);

        this.tested = new CreateConnectorAction(definitionUtils,
                                                clientFactoryManager,
                                                graphBoundsIndexer,
                                                connectorDragProxyFactory,
                                                edgeBuilderControl,
                                                sessionCommandManager,
                                                translationService,
                                                handler -> canvasHighlight)
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
               times(1)).getValue(eq(CreateConnectorAction.KEY_TITLE));
    }

    @Test
    public void testGlyph() {
        assertEquals(EDGE_ID,
                     tested.getGlyphId(canvasHandler,
                                       NODE_UUID));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAction() {
        final DragProxyCallback callback = testStartDrag();
        testMoveDrag(callback);
        testCompleteDrag(callback);
    }

    private void testCompleteDrag(DragProxyCallback callback) {
        // Verify drag proxy complete.
        callback.onComplete(100,
                            500);
        final ArgumentCaptor<EdgeBuildRequest> edgeBuildRequestArgumentCaptor =
                ArgumentCaptor.forClass(EdgeBuildRequest.class);
        final ArgumentCaptor<BuilderControl.BuildCallback> completeCallbackCaptor =
                ArgumentCaptor.forClass(BuilderControl.BuildCallback.class);
        verify(edgeBuilderControl,
               times(1)).build(edgeBuildRequestArgumentCaptor.capture(),
                               completeCallbackCaptor.capture());
        final EdgeBuildRequest edgeBuildRequest = edgeBuildRequestArgumentCaptor.getValue();
        assertEquals(edge,
                     edgeBuildRequest.getEdge());
        assertEquals(element,
                     edgeBuildRequest.getInNode());
        assertEquals(targetNode,
                     edgeBuildRequest.getOutNode());
        final BuilderControl.BuildCallback completeCallback = completeCallbackCaptor.getValue();
        completeCallback.onSuccess(TARGET_NODE_UUID);
        verify(edgeBuilderControl,
               times(1)).disable();
        verify(edgeBuilderControl,
               times(1)).setCommandManagerProvider(eq(null));
        verify(canvasHighlight,
               times(1)).destroy();
    }

    private void testMoveDrag(DragProxyCallback callback) {
        // Verify drag proxy move.
        doAnswer(invocationOnMock -> {
            final EdgeBuildRequest request = (EdgeBuildRequest) invocationOnMock.getArguments()[0];
            return edge == request.getEdge()
                    && element == request.getInNode()
                    && targetNode == request.getOutNode();
        }).when(edgeBuilderControl)
                .allows(any(EdgeBuildRequest.class));
        callback.onMove(100,
                        500);
        verify(edgeBuilderControl,
               times(1)).allows(any(EdgeBuildRequest.class));
        verify(canvasHighlight,
               times(1)).unhighLight();
        verify(canvasHighlight,
               times(1)).highLight(eq(targetNode));
    }

    private DragProxyCallback testStartDrag() {
        final MouseClickEvent event = mock(MouseClickEvent.class);
        when(event.getX()).thenReturn(100d);
        when(event.getY()).thenReturn(500d);
        when(graphBoundsIndexer.getAt(eq(100d),
                                      eq(500d)))
                .thenReturn(targetNode);
        ToolboxAction<AbstractCanvasHandler> cascade =
                tested.onMouseClick(canvasHandler,
                                    NODE_UUID,
                                    event);
        assertEquals(tested,
                     cascade);
        verify(edge,
               times(1)).setSourceNode(eq(element));
        verify(connectorDragProxyFactory,
               times(1)).proxyFor(eq(canvasHandler));
        ArgumentCaptor<DragProxyCallback> proxyArgumentCaptor =
                ArgumentCaptor.forClass(DragProxyCallback.class);
        verify(connectorDragProxyFactory,
               times(1)).show(any(ConnectorDragProxy.Item.class),
                              eq(100),
                              eq(500),
                              proxyArgumentCaptor.capture());

        final DragProxyCallback callback = proxyArgumentCaptor.getValue();

        // Verify drag proxy start.
        callback.onStart(0,
                         0);
        assertNotNull(tested.getCanvasHighlight());
        verify(graphBoundsIndexer,
               times(1)).setRootUUID(eq(ROOT_UUID));
        verify(graphBoundsIndexer,
               times(1)).build(eq(graph));
        verify(edgeBuilderControl,
               times(1)).enable(eq(canvasHandler));
        ArgumentCaptor<RequiresCommandManager.CommandManagerProvider> providerArgumentCaptor =
                ArgumentCaptor.forClass(RequiresCommandManager.CommandManagerProvider.class);
        verify(edgeBuilderControl,
               times(1)).setCommandManagerProvider(providerArgumentCaptor.capture());
        RequiresCommandManager.CommandManagerProvider cmProvider = providerArgumentCaptor.getValue();
        assertEquals(sessionCommandManager,
                     cmProvider.getCommandManager());
        return callback;
    }

    @Test
    public void testCancelConnector() {
        DragProxyCallback callback = testStartDrag();

        assertTrue(dragProxy == tested.getDragProxy());

        testMoveDrag(callback);
        tested.cancelConnector(cancelCanvasAction);

        verify(dragProxy, times(1)).clear();
        assertNull(tested.getDragProxy());
    }
}
