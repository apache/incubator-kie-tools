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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ResizeNodeCommandTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";
    private static final String DEF_ID = "def-id";
    private static final String W_PROPERTY_ID = "w-property-id";
    private static final String H_PROPERTY_ID = "h-property-id";
    private static final String R_PROPERTY_ID = "r-property-id";
    private static final Bounds ELEMENT_BOUNDS = Bounds.create(10d, 20d, 30d, 40d);

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
    private AbstractCanvas.CanvasView canvasView;

    @Mock
    private CanvasPanel canvasPanel;

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
    private Shape<ShapeView> shape;

    @Mock
    private ShapeView shapeView;

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
    private Edge connectorEdgeTarget;

    @Mock
    private ViewConnector viewConnectorTarget;

    @Mock
    private ConnectorShape connectorShapeTarget;

    @Mock
    private BiFunction<Shape, Integer, Point2D> magnetLocationProvider;

    @Mock
    private Consumer<Shape> onResize;

    private ResizeNodeCommand tested;
    private BoundingBox boundingBox;

    @Before
    @SuppressWarnings("all")
    public void setup() {
        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertyAdapter(anyObject())).thenReturn(propertyAdapter);
        when(definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(DEF_ID));
        when(propertyAdapter.getId(eq(wProperty))).thenReturn(W_PROPERTY_ID);
        when(propertyAdapter.getId(eq(hProperty))).thenReturn(H_PROPERTY_ID);
        when(propertyAdapter.getId(eq(rProperty))).thenReturn(R_PROPERTY_ID);
        when(definitionAdapter.getProperty(eq(definition), eq(W_PROPERTY_ID))).thenReturn((Optional) Optional.of(wProperty));
        when(definitionAdapter.getProperty(eq(definition), eq(H_PROPERTY_ID))).thenReturn((Optional) Optional.of(hProperty));
        when(definitionAdapter.getProperty(eq(definition), eq(R_PROPERTY_ID))).thenReturn((Optional) Optional.of(rProperty));
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
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(canvasPanel);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(element.getOutEdges()).thenReturn(Arrays.asList(dockEdge, connectorEdge));
        when(element.getInEdges()).thenReturn(Collections.singletonList(connectorEdgeTarget));
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

        MagnetConnection magnetConnection = new MagnetConnection.Builder().atX(0).atY(0).magnet(MagnetConnection.MAGNET_CENTER).build();
        MagnetConnection magnetConnectionTarget = new MagnetConnection.Builder().magnet(1).build();
        when(viewConnector.getSourceConnection()).thenReturn(Optional.of(magnetConnection));
        when(viewConnectorTarget.getTargetConnection()).thenReturn(Optional.of(magnetConnectionTarget));
        when(canvas.getShape(CONNECTOR_EDGE_UUID)).thenReturn(connectorShape);
        when(canvas.getShape(CONNECTOR_EDGE_TARGET_UUID)).thenReturn(connectorShapeTarget);

        boundingBox = new BoundingBox(0, 0, 100, 200);
        tested = new ResizeNodeCommand(element,
                                       boundingBox,
                                       magnetLocationProvider,
                                       onResize);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitialize() {
        AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> command = tested.initialize(canvasHandler);

        assertNotNull(command);
        final List commands = command.getCommands();
        assertNotNull(commands);
        assertEquals(0, commands.size());
        verify(magnetLocationProvider, never()).apply(eq(shape), eq(0));
        verify(magnetLocationProvider, never()).apply(eq(shape), eq(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPostOperation() {
        CommandResult<CanvasViolation> result = CanvasCommandResultBuilder.SUCCESS;

        CommandResult<CanvasViolation> cascasded = tested.postOperation(canvasHandler, result, 100, 200);

        assertEquals(result, cascasded);
        verify(onResize, times(1)).accept(eq(shape));
        ArgumentCaptor<Bounds> boundsArgumentCaptor = ArgumentCaptor.forClass(Bounds.class);
        verify(elementContent, times(1)).setBounds(boundsArgumentCaptor.capture());
        assertEquals(Bounds.create(10, 20, 110, 220), boundsArgumentCaptor.getValue());
    }
}
