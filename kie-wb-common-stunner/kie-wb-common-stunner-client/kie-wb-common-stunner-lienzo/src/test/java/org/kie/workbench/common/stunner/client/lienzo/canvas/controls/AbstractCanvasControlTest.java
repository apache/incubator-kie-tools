/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class AbstractCanvasControlTest {

    protected static final String ROOT_UUID = "root-uuid1";
    protected static final String ELEMENT_UUID = "element-uuid1";
    protected static final String DEF_ID = "def-id";

    protected static final BoundsImpl GRAPH_BOUNDS = new BoundsImpl(
            new BoundImpl(1d,
                          2d),
            new BoundImpl(3000d,
                          4000d)
    );
    protected static final BoundsImpl ELEMENT_BOUNDS = new BoundsImpl(
            new BoundImpl(10d,
                          20d),
            new BoundImpl(30d,
                          40d)
    );

    @Mock
    protected RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Mock
    protected CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected WiresCanvas canvas;

    @Mock
    protected Layer layer;

    @Mock
    protected WiresManager wiresManager;

    @Mock
    protected SelectionManager selectionManager;

    @Mock
    protected WiresCompositeControl wiresCompositeControl;

    @Mock
    protected Diagram diagram;

    @Mock
    protected Graph graph;

    @Mock
    protected Index graphIndex;

    @Mock
    protected DefinitionSet graphContent;

    @Mock
    protected Metadata metadata;

    @Mock
    protected Node element;

    @Mock
    protected View elementContent;

    @Mock
    protected Shape<ShapeView> shape;

    @Mock
    protected HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    protected HasControlPoints<ShapeViewExtStub> hasControlPoints;

    @Mock
    protected Object definition;

    @Mock
    protected CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    protected ShapeViewExtStub shapeView;


    public void setUp(){
        this.canvasCommandFactory = spy(new DefaultCanvasCommandFactory(null, null));
        this.shapeView = spy(new ShapeViewExtStub(shapeEventHandler,hasControlPoints));

        when(commandManagerProvider.getCommandManager()).thenReturn(commandManager);

        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.asNode()).thenReturn(element);
        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(definition);
        when(elementContent.getBounds()).thenReturn(ELEMENT_BOUNDS);
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);


        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(eq(ELEMENT_UUID))).thenReturn(element);
        when(graph.getContent()).thenReturn(graphContent);
        when(graphContent.getBounds()).thenReturn(GRAPH_BOUNDS);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(shapeEventHandler.supports(any(ViewEventType.class))).thenReturn(true);
        when(wiresManager.getSelectionManager()).thenReturn(selectionManager);
        when(selectionManager.getControl()).thenReturn(wiresCompositeControl);
    }
}
