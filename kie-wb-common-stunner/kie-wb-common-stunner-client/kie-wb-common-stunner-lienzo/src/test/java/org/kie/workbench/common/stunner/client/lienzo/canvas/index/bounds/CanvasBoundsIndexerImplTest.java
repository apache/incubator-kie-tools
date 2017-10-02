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

package org.kie.workbench.common.stunner.client.lienzo.canvas.index.bounds;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasBoundsIndexerImplTest {

    private static final String SHAPE_UUID = "test";
    @Mock
    private AbstractCanvasHandler abstractCanvasHandler;

    @Mock
    private LienzoLayer lienzoLayer;

    @Mock
    private Layer layer;

    @Mock
    private AbstractCanvas abstractCanvas;

    @Mock
    private Shape shape;

    @Mock
    private WiresUtils.UserData userdata;

    @Mock
    private org.kie.workbench.common.stunner.core.client.shape.Shape canvasShape;

    @Mock
    private Index graphIndex;

    @Mock
    private Node node1;

    @Mock
    private Node parentNode;

    private CanvasBoundsIndexerImpl canvasBoundsIndexerImpl;

    @Before
    public void setup() {
        when(abstractCanvasHandler.getAbstractCanvas()).thenReturn(abstractCanvas);
        when(abstractCanvasHandler.getAbstractCanvas().getLayer()).thenReturn(lienzoLayer);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        when(lienzoLayer.getLienzoLayer().getLayer()).thenReturn(layer);

        when(shape.getUserData()).thenReturn(userdata);

        when(userdata.getUuid()).thenReturn(SHAPE_UUID);
        when(abstractCanvas.getShape(SHAPE_UUID)).thenReturn(canvasShape);

        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        when(abstractCanvasHandler.getGraphIndex().getNode(SHAPE_UUID)).thenReturn(node1);
        when(canvasShape.getUUID()).thenReturn(SHAPE_UUID);

        canvasBoundsIndexerImpl = new CanvasBoundsIndexerImpl();
        canvasBoundsIndexerImpl.build(
                abstractCanvasHandler);
    }

    @Test
    public void testGetAt() {
        when(lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(eq(20),
                                                                      eq(20))).thenReturn(shape);
        Node<View<?>, Edge> node = canvasBoundsIndexerImpl.getAt(20,
                                                                 20);
        assertNotNull(node);
        Node<View<?>, Edge> nodeAtFreePosition = canvasBoundsIndexerImpl.getAt(400,
                                                                               400);
        assertNull(nodeAtFreePosition);
    }

    @Test
    public void testGetAreaAtUL() {
        when(lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(eq(20),
                                                                      eq(20))).thenReturn(shape);
        Node<View<?>, Edge> node = canvasBoundsIndexerImpl.getAt(20,
                                                                 20,
                                                                 100,
                                                                 100,
                                                                 parentNode);
        assertNotNull(node);
    }

    @Test
    public void testGetAreaAtUR() {
        when(lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(eq(120),
                                                                      eq(20))).thenReturn(shape);
        Node<View<?>, Edge> node = canvasBoundsIndexerImpl.getAt(20,
                                                                 20,
                                                                 100,
                                                                 100,
                                                                 parentNode);
        assertNotNull(node);
    }

    @Test
    public void testGetAreaAtCC() {
        when(lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(eq(70),
                                                                      eq(70))).thenReturn(shape);
        Node<View<?>, Edge> node = canvasBoundsIndexerImpl.getAt(20,
                                                                 20,
                                                                 100,
                                                                 100,
                                                                 parentNode);
        assertNotNull(node);
    }

    @Test
    public void testGetAreaAtWhitParentLL() {
        when(lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(eq(20),
                                                                      eq(120))).thenReturn(shape);
        Node<View<?>, Edge> node = canvasBoundsIndexerImpl.getAt(20,
                                                                 20,
                                                                 100,
                                                                 100,
                                                                 parentNode);
        assertNotNull(node);
    }

    @Test
    public void testGetAreaAtWhitParentLR() {
        when(lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(eq(120),
                                                                      eq(120))).thenReturn(shape);
        Node<View<?>, Edge> node = canvasBoundsIndexerImpl.getAt(20,
                                                                 20,
                                                                 100,
                                                                 100,
                                                                 parentNode);
        assertNotNull(node);
    }

    @Test
    public void testGetAreaAtFreePosition() {
        Node<View<?>, Edge> nodeFreePosition = canvasBoundsIndexerImpl.getAt(0,
                                                                             600,
                                                                             100,
                                                                             10,
                                                                             parentNode);
        assertNull(nodeFreePosition);
    }

    @Test
    public void testDestroy() {
        canvasBoundsIndexerImpl.destroy();
        assertNull(canvasBoundsIndexerImpl.canvasHandler);
    }
}