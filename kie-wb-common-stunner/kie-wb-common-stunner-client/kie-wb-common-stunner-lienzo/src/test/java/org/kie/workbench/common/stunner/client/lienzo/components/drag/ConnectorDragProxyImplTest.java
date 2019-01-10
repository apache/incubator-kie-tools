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
package org.kie.workbench.common.stunner.client.lienzo.components.drag;

import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.ShapeViewDragProxy;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ConnectorDragProxyImplTest {

    @Mock
    private ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory;

    @Mock
    private GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    private ConnectorDragProxy.Item item;

    @Mock
    private DragProxyCallback dragProxyCallback;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private WiresCanvas wiresCanvas;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private MagnetManager magnetManager;

    @Mock
    private MagnetManager.Magnets transientShapeMagnets;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Shape shape;

    @Mock
    private Node sourceNode;

    private ConnectorDragProxyImpl connectorDragProxyImpl;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private Edge edge;

    @Mock
    private View content;

    @Mock
    private View definition;

    @Mock
    private EdgeShape edgeShape;

    @Mock
    private WiresConnectorView shapeView;

    @Mock
    private Group group;

    @Mock
    MultiPath head;

    @Mock
    MultiPath tail;

    @Mock
    IDirectionalMultiPointShape line;

    @Mock
    Attributes lineAttributes;

    @Before
    public void setup() {
        when(item.getSourceNode()).thenReturn(sourceNode);
        when(item.getShapeFactory()).thenReturn(shapeFactory);
        when(shapeFactory.newShape(any())).thenReturn(edgeShape);
        when(edge.getContent()).thenReturn(content);
        when(edgeShape.getShapeView()).thenReturn(shapeView);

        when(shapeView.getGroup()).thenReturn(group);

        when(shapeView.getLine()).thenReturn(line);
        when(shapeView.uuid()).thenReturn("uuid");

        when(line.getAttributes()).thenReturn(lineAttributes);
        when(shapeView.getHead()).thenReturn(head);
        when(shapeView.getTail()).thenReturn(tail);
        when(content.getDefinition()).thenReturn(definition);
        when(item.getEdge()).thenReturn(edge);
        when(sourceNode.getUUID()).thenReturn("uuid");

        when(canvasHandler.getAbstractCanvas()).thenReturn(wiresCanvas);
        when(wiresCanvas.getShape(any())).thenReturn(shape);
        when(wiresCanvas.getWiresManager()).thenReturn(wiresManager);

        when(wiresManager.getMagnetManager()).thenReturn(magnetManager);
        when(magnetManager.createMagnets(any(), any())).thenReturn(transientShapeMagnets);
        when(magnetManager.createMagnets(any())).thenReturn(transientShapeMagnets);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn("uuid");

        connectorDragProxyImpl = new ConnectorDragProxyImpl(shapeViewDragProxyFactory, graphBoundsIndexer);
    }

    @Test
    public void testIfTransientShapeAreDestroyed() {
        connectorDragProxyImpl.proxyFor(canvasHandler);
        connectorDragProxyImpl.show(item,
                                    0,
                                    0,
                                    dragProxyCallback);

        connectorDragProxyImpl.deregisterTransientConnector();
        verify(transientShapeMagnets).destroy();
    }
}