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


package com.ait.lienzo.client.widget;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DragContextTest {

    private final double ALPHA = 50;
    private final int DRAG_START_X = 10;
    private final int DRAG_START_Y = 20;
    private final double PRIM_X = 15;
    private final double PRIM_Y = 25;

    @Mock
    private IPrimitive<?> primitive;

    @Mock
    private Node node;

    @Mock
    private Node parentNode;

    private DragContext tested;

    @Before
    public void setUp() {
        when(primitive.asNode()).thenReturn(node);
        when(primitive.getParent()).thenReturn(parentNode);
        when(primitive.getX()).thenReturn(PRIM_X);
        when(primitive.getY()).thenReturn(PRIM_Y);
        when(node.getParent()).thenReturn(parentNode);
        when(parentNode.getAbsoluteTransform()).thenReturn(new Transform());
        when(parentNode.getParent()).thenReturn(null);
        when(parentNode.getAlpha()).thenReturn(ALPHA);
        when(parentNode.getNodeType()).thenReturn(NodeType.LAYER);

        tested = spy(new DragContext(DRAG_START_X, DRAG_START_Y, primitive));
    }

    @Test
    public void testDrawNodeWithTransforms() {
        Context2D context = mock(Context2D.class);
        tested.drawNodeWithTransforms(context);
        verify(context).save();
        verify(context).transform(tested.m_ltog);
        verify(primitive).drawWithTransforms(context, ALPHA, null);
        verify(context).restore();
    }

    @Test
    public void testGetNodeParentsAlpha() {
        double expected1 = 1;
        Node node1 = mock(Node.class);
        when(node1.getParent()).thenReturn(null);

        double expected2 = 0.75;
        Node node2 = mock(Node.class);
        Node parentNode2 = mock(Node.class);
        when(node2.getParent()).thenReturn(parentNode2);
        when(parentNode2.getParent()).thenReturn(null);
        when(parentNode2.getAlpha()).thenReturn(expected2);
        when(parentNode2.getNodeType()).thenReturn(NodeType.LAYER);

        double expected3 = 0.125;
        double parentAlpha3 = 0.5;
        double grandparentAlpha3 = 0.25;
        Node node3 = mock(Node.class);
        Node parentNode3 = mock(Node.class);
        Node grandparentNode3 = mock(Node.class);
        when(node3.getParent()).thenReturn(parentNode3);
        when(parentNode3.getAlpha()).thenReturn(parentAlpha3);
        when(parentNode3.getParent()).thenReturn(grandparentNode3);
        when(grandparentNode3.getAlpha()).thenReturn(grandparentAlpha3);
        when(grandparentNode3.getNodeType()).thenReturn(NodeType.GROUP);
        when(grandparentNode3.getParent()).thenReturn(null);

        double expected4 = 0.7;
        double grandparentAlpha4 = 0.4;
        Node node4 = mock(Node.class);
        Node parentNode4 = mock(Node.class);
        Node grandparentNode4 = mock(Node.class);
        when(node4.getParent()).thenReturn(parentNode4);
        when(parentNode4.getAlpha()).thenReturn(expected4);
        when(parentNode4.getParent()).thenReturn(grandparentNode4);
        when(grandparentNode4.getAlpha()).thenReturn(grandparentAlpha4);
        when(grandparentNode4.getNodeType()).thenReturn(NodeType.LAYER);
        when(grandparentNode4.getParent()).thenReturn(null);

        double result1 = tested.getNodeParentsAlpha(node1);
        assertEquals(expected1, result1, Double.MIN_NORMAL);

        double result2 = tested.getNodeParentsAlpha(node2);
        assertEquals(expected2, result2, Double.MIN_NORMAL);

        double result3 = tested.getNodeParentsAlpha(node3);
        assertEquals(expected3, result3, Double.MIN_NORMAL);

        double result4 = tested.getNodeParentsAlpha(node4);
        assertEquals(expected4, result4, Double.MIN_NORMAL);
    }

    @Test
    public void testDragMoveUpdate() {
        int x = 20;
        int y = 30;

        tested.dragMoveUpdate(x, y);
        assertEquals(x, tested.m_evtx);
        assertEquals(y, tested.m_evty);
        verify(tested).dragUpdate();
    }

    @Test
    public void testDragOffsetUpdate() {
        int offsetX = 40;
        int offsetY = 50;

        tested.dragMoveUpdate(offsetX, offsetY);
        assertEquals(offsetX, tested.m_evtx);
        assertEquals(offsetY, tested.m_evty);
        verify(tested).dragUpdate();
    }

    @Test
    public void testDragUpdate() {
        tested.m_evtx = 15;
        tested.m_evty = 25;
        tested.m_offsetx = 5;
        tested.m_offsety = 35;
        tested.dragUpdate();

        assertEquals(tested.m_evtx - tested.m_begx, tested.m_dstx);
        assertEquals(tested.m_evty - tested.m_begy, tested.m_dsty);

        verify(tested.m_prim).setX(tested.m_prmx + tested.m_lclp.getX());
        verify(tested.m_prim).setY(tested.m_prmy + tested.m_lclp.getY());
    }

    @Test
    public void testDragDone() {
        tested.m_lstx = PRIM_X;
        tested.m_lsty = PRIM_Y;
        tested.dragDone();
        verify(tested.m_prim, never()).setX(anyDouble());

        tested.m_lstx = 10;
        tested.m_lsty = 15;
        tested.dragDone();
        verify(tested.m_prim).setX(tested.m_prmx + tested.m_lclp.getX());
        verify(tested.m_prim).setY(tested.m_prmy + tested.m_lclp.getY());
    }

    @Test
    public void testReset() {
        tested.reset();
        verify(tested.m_prim).setX(tested.m_prmx);
        verify(tested.m_prim).setY(tested.m_prmy);
    }

    @Test
    public void testGetDragStartX() {
        assertEquals(tested.m_begx, tested.getDragStartX(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetDragStartY() {
        assertEquals(tested.m_begy, tested.getDragStartY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetEventX() {
        assertEquals(tested.m_evtx, tested.getEventX(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetEventY() {
        assertEquals(tested.m_evty, tested.getEventY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetOffsetX() {
        assertEquals(tested.m_offsetx, tested.getOffsetX(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetOffsetY() {
        assertEquals(tested.m_offsety, tested.getOffsetY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetDx() {
        assertEquals(tested.m_dstx, tested.getDx(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetDy() {
        assertEquals(tested.m_dsty, tested.getDy(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetGlobalToLocal() {
        assertEquals(tested.m_gtol, tested.getGlobalToLocal());
    }

    @Test
    public void testGetLocalToGlobal() {
        assertEquals(tested.m_ltog, tested.getLocalToGlobal());
    }

    @Test
    public void testGetViewportToGlobal() {
        assertEquals(tested.m_vtog, tested.getViewportToGlobal());
    }

    @Test
    public void testGetLocalAdjusted() {
        assertEquals(tested.m_lclp, tested.getLocalAdjusted());
    }

    @Test
    public void testGetStartAdjusted() {
        Point2D result = tested.getStartAdjusted();
        assertEquals(DRAG_START_X, result.getX(), Double.MIN_NORMAL);
        assertEquals(DRAG_START_Y, result.getY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetEventAdjusted() {
        int expectedX = 16;
        int expectedY = 7;
        tested.m_evtx = expectedX;
        tested.m_evty = expectedY;

        Point2D result = tested.getEventAdjusted();
        assertEquals(expectedX, result.getX(), Double.MIN_NORMAL);
        assertEquals(expectedY, result.getY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetOffset() {
        int expectedX = 16;
        int expectedY = 7;
        tested.m_offsetx = expectedX;
        tested.m_offsety = expectedY;

        Point2D result = tested.getOffset();
        assertEquals(expectedX, result.getX(), Double.MIN_NORMAL);
        assertEquals(expectedY, result.getY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetDistanceAdjusted() {
        tested.m_evtx = 15;
        tested.m_evty = 13;
        tested.m_offsetx = 22;
        tested.m_offsety = 25;

        Point2D result = tested.getDistanceAdjusted();
        assertEquals(27, result.getX(), Double.MIN_NORMAL);
        assertEquals(18, result.getY(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetNode() {
        assertEquals(tested.m_prim, tested.getNode());
    }

    @Test
    public void testGetDragConstraints() {
        assertEquals(tested.m_drag, tested.getDragConstraints());
    }
}