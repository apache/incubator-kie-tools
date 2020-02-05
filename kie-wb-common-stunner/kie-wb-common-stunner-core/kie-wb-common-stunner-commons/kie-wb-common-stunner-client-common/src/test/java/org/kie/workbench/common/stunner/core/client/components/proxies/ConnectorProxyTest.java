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

package org.kie.workbench.common.stunner.core.client.components.proxies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorProxyTest {

    public static final String SHAPE_SET_ID = "ss1";
    public static final String EDGE_ID = "edge1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> selectionEvent;

    @Mock
    private DefaultCanvasCommandFactory commandFactory;
    private ManagedInstanceStub<DefaultCanvasCommandFactory> commandFactories;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Canvas canvas;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private EdgeShape connector;

    private ConnectorProxy tested;
    private ElementProxy proxy;
    private ElementProxyTest.ElementProxyViewMock<EdgeShape> view;
    private Edge<ViewConnector<?>, Node> edge;
    private Node<View<?>, Edge> sourceNode;

    @Before
    public void setUp() {
        commandFactories = new ManagedInstanceStub<>(commandFactory);
        sourceNode = new NodeImpl<>("sourceNode");
        sourceNode.setContent(new ViewImpl<>(mock(Object.class),
                                             Bounds.create()));
        edge = new EdgeImpl<>(EDGE_ID);
        proxy = spy(new ElementProxy(commandManager, selectionEvent, commandFactories, definitionUtils));
        view = spy(new ElementProxyTest.ElementProxyViewMock<>());
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(canvas.getShape(eq(EDGE_ID))).thenReturn(connector);
        tested = new ConnectorProxy(proxy, view)
                .setCanvasHandler(canvasHandler)
                .setSourceNode(sourceNode)
                .setEdge(edge);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(proxy, times(1)).setView(eq(view));
        verify(proxy, times(1)).setProxyBuilder(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStart() {
        CanvasCommand<AbstractCanvasHandler> command = mock(CanvasCommand.class);
        doReturn(command).when(commandFactory).addConnector(eq(sourceNode),
                                                            eq(edge),
                                                            any(MagnetConnection.class),
                                                            eq(SHAPE_SET_ID));
        double x = 1d;
        double y = 2d;
        tested.init();
        tested.start(x, y);
        verify(proxy, times(1)).start(eq(x), eq(y));
        EdgeShape edgeShape = view.getShapeBuilder().get();
        assertEquals(connector, edgeShape);
        verify(proxy, times(1)).execute(eq(command));
    }

    @Test
    public void testDestroy() {
        tested.init();
        tested.destroy();
        verify(proxy, times(1)).destroy();
    }

    @Test
    public void testCancelKey() {
        KeyDownEvent event = new KeyDownEvent(KeyboardEvent.Key.ESC);
        tested.init();
        tested.onKeyDownEvent(event);
        tested.onKeyDownEvent(new KeyDownEvent(KeyboardEvent.Key.CONTROL));
        tested.onKeyDownEvent(new KeyDownEvent(KeyboardEvent.Key.ALT));
        tested.onKeyDownEvent(new KeyDownEvent(KeyboardEvent.Key.DELETE));
        verify(proxy, times(1)).destroy();
    }
}
