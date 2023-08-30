/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.components.proxies;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCommand;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeProxyTest {

    private static final String SHAPE_SET_ID = "ss1";
    private static final String PARENT_NODE_ID = "parent1";
    private static final String TARGET_NODE_ID = "target1";
    private static final String EDGE_ID = "edge1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

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
    private AbstractCanvas canvas;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private EdgeShape connector;

    @Mock
    private NodeShape targetShape;

    @Mock
    private SessionManager sessionManager;

    private NodeProxy tested;
    private ElementProxy proxy;
    private ElementProxyTest.ElementProxyViewMock<NodeShape> view;
    private Edge<ViewConnector<?>, Node> edge;
    private Graph graph;
    private Node<View<?>, Edge> sourceNode;
    private Node<View<?>, Edge> targetNode;

    @Before
    @SuppressWarnings("all")
    public void setUp() {
        commandFactories = new ManagedInstanceStub<>(commandFactory);
        sourceNode = new NodeImpl<>("sourceNode");
        sourceNode.setContent(new ViewImpl<>(mock(Object.class),
                                             Bounds.createEmpty()));
        targetNode = new NodeImpl<>(TARGET_NODE_ID);
        targetNode.setContent(new ViewImpl<>(mock(Object.class),
                                             Bounds.createEmpty()));
        edge = new EdgeImpl<>(EDGE_ID);
        graph = new GraphImpl<>("graphUUID", new GraphNodeStoreImpl());
        graph.addNode(sourceNode);
        proxy = spy(new ElementProxy(commandManager, selectionEvent, commandFactories, definitionUtils, sessionManager));
        view = spy(new ElementProxyTest.ElementProxyViewMock<>());
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        doNothing().when(proxy).handleCancelKey();
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(canvas.getShape(eq(EDGE_ID))).thenReturn(connector);
        when(canvas.getShape(eq(TARGET_NODE_ID))).thenReturn(targetShape);
        tested = new NodeProxy(proxy, view)
                .setCanvasHandler(canvasHandler)
                .setSourceNode(sourceNode)
                .setEdge(edge)
                .setTargetNode(targetNode);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(proxy, times(1)).setView(eq(view));
        verify(proxy, times(1)).setProxyBuilder(any());
    }

    @Test
    @SuppressWarnings("all")
    public void testCreateTargetNode() {
        CanvasCommand<AbstractCanvasHandler> addConnector = mock(CanvasCommand.class);
        CanvasCommand<AbstractCanvasHandler> addNode = mock(CanvasCommand.class);
        CanvasCommand<AbstractCanvasHandler> setTargetNode = mock(CanvasCommand.class);
        doReturn(addConnector).when(commandFactory).addConnector(eq(sourceNode),
                                                                 eq(edge),
                                                                 Mockito.<MagnetConnection>any(),
                                                                 eq(SHAPE_SET_ID));
        doReturn(addNode).when(commandFactory).addNode(eq(targetNode),
                                                       eq(SHAPE_SET_ID));
        doReturn(setTargetNode).when(commandFactory).setTargetNode(eq(targetNode),
                                                                   eq(edge),
                                                                   any());
        verifyCreateTargetNode(addConnector, addNode, setTargetNode);
    }

    @Test
    @SuppressWarnings("all")
    public void testCreateTargetNodeInSomeParent() {
        Node<View<?>, Edge> parentNode = new NodeImpl<>(PARENT_NODE_ID);
        parentNode.setContent(new ViewImpl<>(mock(Object.class),
                                             Bounds.createEmpty()));
        DirectGraphCommandExecutionContext context = new DirectGraphCommandExecutionContext(definitionManager,
                                                                                            factoryManager,
                                                                                            new MapIndexBuilder().build(graph));
        new AddNodeCommand(parentNode).execute(context);
        new SetChildrenCommand(parentNode, sourceNode).execute(context);
        CanvasCommand<AbstractCanvasHandler> addConnector = mock(CanvasCommand.class);
        CanvasCommand<AbstractCanvasHandler> addNode = mock(CanvasCommand.class);
        CanvasCommand<AbstractCanvasHandler> setTargetNode = mock(CanvasCommand.class);
        doReturn(addConnector).when(commandFactory).addConnector(eq(sourceNode),
                                                                 eq(edge),
                                                                 Mockito.<MagnetConnection>any(),
                                                                 eq(SHAPE_SET_ID));
        doReturn(addNode).when(commandFactory).addChildNode(eq(parentNode),
                                                            eq(targetNode),
                                                            eq(SHAPE_SET_ID));
        doReturn(setTargetNode).when(commandFactory).setTargetNode(eq(targetNode),
                                                                   eq(edge),
                                                                   any());
        verifyCreateTargetNode(addConnector, addNode, setTargetNode);
    }

    @SuppressWarnings("all")
    private void verifyCreateTargetNode(CanvasCommand<AbstractCanvasHandler> addConnectorCommand,
                                        CanvasCommand<AbstractCanvasHandler> addNodeCommand,
                                        CanvasCommand<AbstractCanvasHandler> setTargetNodeCommand) {
        tested.init();
        tested.start(1d, 2d);
        verify(proxy, times(1)).start(eq(1d), eq(2d));
        NodeShape targetNodeShape = view.getShapeBuilder().get();
        assertEquals(targetShape, targetNodeShape);
        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(proxy, times(1)).execute(commandCaptor.capture());
        DeferredCompositeCommand command = (DeferredCompositeCommand) commandCaptor.getValue();
        List commands = command.getCommands();
        assertEquals(3, command.size());
        DeferredCommand c0 = (DeferredCommand) commands.get(0);
        assertEquals(addNodeCommand, c0.getCommand());
        DeferredCommand c1 = (DeferredCommand) commands.get(1);
        assertEquals(addConnectorCommand, c1.getCommand());
        DeferredCommand c2 = (DeferredCommand) commands.get(2);
        assertEquals(setTargetNodeCommand, c2.getCommand());
    }

    @Test
    public void testDestroy() {
        tested.init();
        tested.destroy();
        verify(proxy, times(1)).destroy();
    }
}
