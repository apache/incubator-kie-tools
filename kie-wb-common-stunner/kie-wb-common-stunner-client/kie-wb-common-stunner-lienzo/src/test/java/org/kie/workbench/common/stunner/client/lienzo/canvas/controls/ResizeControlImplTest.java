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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ResizeControlImplTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";
    private static final String DEF_ID = "def-id";
    private static final String W_PROPERTY_ID = "w-property-id";
    private static final String H_PROPERTY_ID = "h-property-id";
    private static final String R_PROPERTY_ID = "r-property-id";
    private static final BoundsImpl ELEMENT_BOUNDS = new BoundsImpl(
            new BoundImpl(10d,
                          20d),
            new BoundImpl(30d,
                          40d)
    );

    private static final String DOCKED_NODE_UUID = UUID.uuid();
    private static final String CONNECTOR_EDGE_UUID = UUID.uuid();
    private static final String CONNECTOR_EDGE_TARGET_UUID = UUID.uuid();
    private static final Double SHAPE_X = 0d;
    private static final Double SHAPE_Y = 0d;
    public static final double NEW_CONNECTION_X = 10d;
    public static final double NEW_CONNECTION_Y = 20d;
    public static final double NEW_CONNECTION_X_TARGET = 30d;
    public static final double NEW_CONNECTION_Y_TARGET = 40d;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private DefinitionSet graphContent;

    @Mock
    private Metadata metadata;

    @Mock
    private Node element;

    @Mock
    private View elementContent;

    @Mock
    private Shape<WiresShapeView> shape;

    @Mock
    private Object definition;

    @Mock
    private Object wProperty;

    @Mock
    private Object hProperty;

    @Mock
    private Object rProperty;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private AdapterRegistry adapterRegistry;

    @Mock
    private DefinitionAdapter<Object> definitionAdapter;

    @Mock
    private PropertyAdapter propertyAdapter;

    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private WiresShapeViewExt shapeView;

    private ResizeControlImpl tested;

    @Mock
    private Edge dockEdge;

    @Mock
    private Node dockedNode;

    @Mock
    private Shape dockedShape;

    @Mock
    private ShapeView dockedShapeView;

    @Mock
    private Edge connectorEdge;

    @Mock
    private ViewConnector viewConnector;

    @Mock
    private ConnectorShape connectorShape;

    @Mock
    private MagnetManager.Magnets magnets;

    @Mock
    private WiresMagnet magnet;

    private MagnetConnection magnetConnection;

    @Mock
    private Edge connectorEdgeTarget;

    @Mock
    private ViewConnector viewConnectorTarget;

    @Mock
    private ConnectorShape connectorShapeTarget;

    @Mock
    private WiresMagnet magnetTarget;

    private MagnetConnection magnetConnectionTarget;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.canvasCommandFactory = spy(new DefaultCanvasCommandFactory(null, null));

        when(shapeView.supports(ViewEventType.RESIZE)).thenReturn(true);
        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertyAdapter(anyObject())).thenReturn(propertyAdapter);
        when(definitionAdapter.getId(eq(definition))).thenReturn(DEF_ID);
        when(propertyAdapter.getId(eq(wProperty))).thenReturn(W_PROPERTY_ID);
        when(propertyAdapter.getId(eq(hProperty))).thenReturn(H_PROPERTY_ID);
        when(propertyAdapter.getId(eq(rProperty))).thenReturn(R_PROPERTY_ID);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.WIDTH),
                                               eq(definition))).thenReturn(wProperty);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.HEIGHT),
                                               eq(definition))).thenReturn(hProperty);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.RADIUS),
                                               eq(definition))).thenReturn(rProperty);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(definition);
        when(elementContent.getBounds()).thenReturn(ELEMENT_BOUNDS);
        when(graph.getContent()).thenReturn(graphContent);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(element.getOutEdges()).thenReturn(Arrays.asList(dockEdge, connectorEdge));
        when(element.getInEdges()).thenReturn(Arrays.asList(connectorEdgeTarget));
        when(dockEdge.getContent()).thenReturn(new Dock());
        when(dockEdge.getTargetNode()).thenReturn(dockedNode);
        when(dockedNode.getUUID()).thenReturn(DOCKED_NODE_UUID);
        when(canvas.getShape(DOCKED_NODE_UUID)).thenReturn(dockedShape);
        when(dockedShape.getShapeView()).thenReturn(dockedShapeView);
        when(dockedShapeView.getShapeX()).thenReturn(SHAPE_X);
        when(dockedShapeView.getShapeY()).thenReturn(SHAPE_Y);

        when(connectorEdge.getSourceNode()).thenReturn(element);
        when(connectorEdge.getContent()).thenReturn(viewConnector);
        when(connectorEdge.getUUID()).thenReturn(CONNECTOR_EDGE_UUID);
        when(connectorEdgeTarget.getTargetNode()).thenReturn(element);
        when(connectorEdgeTarget.getContent()).thenReturn(viewConnectorTarget);
        when(connectorEdgeTarget.getUUID()).thenReturn(CONNECTOR_EDGE_TARGET_UUID);

        magnetConnection = new MagnetConnection.Builder().atX(0).atY(0).magnet(MagnetConnection.MAGNET_CENTER).build();
        magnetConnectionTarget = new MagnetConnection.Builder().magnet(1).build();
        when(viewConnector.getSourceConnection()).thenReturn(Optional.of(magnetConnection));
        when(viewConnectorTarget.getTargetConnection()).thenReturn(Optional.of(magnetConnectionTarget));
        when(canvas.getShape(CONNECTOR_EDGE_UUID)).thenReturn(connectorShape);
        when(canvas.getShape(CONNECTOR_EDGE_TARGET_UUID)).thenReturn(connectorShapeTarget);
        when(shapeView.getMagnets()).thenReturn(magnets);
        when(magnets.getMagnet(MagnetConnection.MAGNET_CENTER)).thenReturn(magnet);
        when(magnets.getMagnet(1)).thenReturn(magnetTarget);
        when(magnet.getX()).thenReturn(NEW_CONNECTION_X);
        when(magnet.getY()).thenReturn(NEW_CONNECTION_Y);
        when(magnetTarget.getX()).thenReturn(NEW_CONNECTION_X_TARGET);
        when(magnetTarget.getY()).thenReturn(NEW_CONNECTION_Y_TARGET);

        this.tested = new ResizeControlImpl(canvasCommandFactory);
        tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testRegister() {
        tested.init(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeView,
               times(1)).supports(eq(ViewEventType.RESIZE));
        verify(shapeView,
               times(1)).addHandler(eq(ViewEventType.RESIZE),
                                    any(ResizeHandler.class));
        assertTrue(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(shapeView,
               times(1)).removeHandler(any(ViewHandler.class));
        assertFalse(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResize() {
        when(commandManager.execute(eq(canvasHandler),
                                    any(Command.class))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.init(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeView,
               times(1)).supports(eq(ViewEventType.RESIZE));
        final ArgumentCaptor<ResizeHandler> resizeHandlerArgumentCaptor =
                ArgumentCaptor.forClass(ResizeHandler.class);
        verify(shapeView,
               times(1)).addHandler(eq(ViewEventType.RESIZE),
                                    resizeHandlerArgumentCaptor.capture());

        //assert initial connection position
        assertEquals(magnetConnection.getLocation().getX(), 0, 0);
        assertEquals(magnetConnection.getLocation().getY(), 0, 0);
        assertNull(magnetConnectionTarget.getLocation());

        final ResizeHandler resizeHandler = resizeHandlerArgumentCaptor.getValue();
        final double x = 121.45d;
        final double y = 23.456d;
        final double width = 100d;
        final double height = 200d;
        final ResizeEvent event = new ResizeEvent(x,
                                                  y,
                                                  x,
                                                  y,
                                                  width,
                                                  height);
        resizeHandler.end(event);
        final ArgumentCaptor<Command> commandArgumentCaptor =
                ArgumentCaptor.forClass(Command.class);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 commandArgumentCaptor.capture());
        final Command command = commandArgumentCaptor.getValue();
        assertNotNull(command);
        assertTrue(command instanceof AbstractCompositeCommand);
        final List commands = ((AbstractCompositeCommand) command).getCommands();
        assertNotNull(commands);
        assertEquals(7,
                     commands.size());
        assertTrue(commands.get(0) instanceof UpdateElementPositionCommand);
        final UpdateElementPositionCommand positionCommand = (UpdateElementPositionCommand) commands.get(0);
        assertEquals(element,
                     positionCommand.getElement());
        assertEquals(new Point2D(x, y),
                     positionCommand.getLocation());
        assertTrue(commands.get(1) instanceof UpdateElementPropertyCommand);
        final UpdateElementPropertyCommand wPropertyCommand = (UpdateElementPropertyCommand) commands.get(1);
        assertEquals(element,
                     wPropertyCommand.getElement());
        assertEquals(W_PROPERTY_ID,
                     wPropertyCommand.getPropertyId());
        assertEquals(width,
                     wPropertyCommand.getValue());
        assertTrue(commands.get(2) instanceof UpdateElementPropertyCommand);
        final UpdateElementPropertyCommand hPropertyCommand = (UpdateElementPropertyCommand) commands.get(2);
        assertEquals(element,
                     hPropertyCommand.getElement());
        assertEquals(H_PROPERTY_ID,
                     hPropertyCommand.getPropertyId());
        assertEquals(height,
                     hPropertyCommand.getValue());
        assertTrue(commands.get(3) instanceof UpdateElementPropertyCommand);
        final UpdateElementPropertyCommand rPropertyCommand = (UpdateElementPropertyCommand) commands.get(3);
        assertEquals(element,
                     rPropertyCommand.getElement());
        assertEquals(R_PROPERTY_ID,
                     rPropertyCommand.getPropertyId());
        assertEquals(50d,
                     rPropertyCommand.getValue());

        //test parent with docked node resize
        ArgumentCaptor<Point2D> shapePoint2DArgumentCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(canvasCommandFactory, times(1)).updatePosition(eq(dockedNode), shapePoint2DArgumentCaptor.capture());
        Point2D shapePoint = shapePoint2DArgumentCaptor.getValue();
        assertEquals(shapePoint.getX(), SHAPE_X, 0);
        assertEquals(shapePoint.getY(), SHAPE_Y, 0);

        //test connections position on the node resize
        //source connection
        verify(canvasCommandFactory, times(1)).setSourceNode(element, connectorEdge, magnetConnection);
        assertEquals(magnetConnection.getLocation().getX(), NEW_CONNECTION_X, 0);
        assertEquals(magnetConnection.getLocation().getY(), NEW_CONNECTION_Y, 0);

        //target connection
        verify(canvasCommandFactory, times(1)).setTargetNode(element, connectorEdgeTarget, magnetConnectionTarget);
        //assert new connection position after resize
        assertEquals(magnetConnectionTarget.getLocation().getX(), NEW_CONNECTION_X_TARGET, 0);
        assertEquals(magnetConnectionTarget.getLocation().getY(), NEW_CONNECTION_Y_TARGET, 0);
    }
}
