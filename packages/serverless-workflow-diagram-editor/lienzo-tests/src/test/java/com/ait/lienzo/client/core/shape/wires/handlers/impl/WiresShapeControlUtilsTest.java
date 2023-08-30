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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeControlUtilsTest {

    @Mock
    private Viewport viewport;

    private Transform transform;

    @Before
    public void setup() {
        transform = new Transform();
        when(viewport.getTransform()).thenReturn(transform);
    }

    @Test
    public void testGetViewportRelativeLocation() {
        transform.scaleWithXY(2, 3).translate(10, 100);
        Point2D location = WiresShapeControlUtils.getViewportRelativeLocation(viewport, 33, 67);
        assertEquals(6.5d, location.getX(), 0d);
        assertEquals(-77.66666666666667d, location.getY(), 0d);
    }

    @Test
    public void testGetViewportRelativeLocationByEvent() {
        AbstractNodeHumanInputEvent event = mock(AbstractNodeHumanInputEvent.class);
        when(event.getX()).thenReturn(33);
        when(event.getY()).thenReturn(67);
        transform.scaleWithXY(2, 3).translate(10, 100);
        Point2D location = WiresShapeControlUtils.getViewportRelativeLocation(viewport, event);
        assertEquals(6.5d, location.getX(), 0d);
        assertEquals(-77.66666666666667d, location.getY(), 0d);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveShapeUpToParent() {
        WiresShape shape = mock(WiresShape.class);
        Group shapeGroup = mock(Group.class);
        when(shape.getGroup()).thenReturn(shapeGroup);
        WiresContainer parent = mock(WiresContainer.class);
        IContainer parentContainer = mock(IContainer.class);
        when(parent.getContainer()).thenReturn(parentContainer);
        WiresShapeControlUtils.moveShapeUpToParent(shape, parent);
        verify(parentContainer, times(1)).moveToTop(eq(shapeGroup));
        verify(parentContainer, never()).moveDown(anyObject());
        verify(parentContainer, never()).moveUp(anyObject());
        verify(parentContainer, never()).moveToBottom(anyObject());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveShapeAndConnectorsTopToParent() {
        WiresShape shape = mock(WiresShape.class);
        Group shapeGroup = mock(Group.class);
        when(shape.getGroup()).thenReturn(shapeGroup);
        WiresContainer parent = mock(WiresContainer.class);
        IContainer parentContainer = mock(IContainer.class);
        when(parent.getContainer()).thenReturn(parentContainer);
        MagnetManager.Magnets magnets = mock(MagnetManager.Magnets.class);
        when(shape.getMagnets()).thenReturn(magnets);
        WiresMagnet magnet = mock(WiresMagnet.class);
        when(magnets.size()).thenReturn(1);
        when(magnets.getMagnet(eq(0))).thenReturn(magnet);
        WiresConnection connection = mock(WiresConnection.class);
        when(magnet.getConnectionsSize()).thenReturn(1);
        NFastArrayList<WiresConnection> list = new NFastArrayList<>();
        list.add(connection);
        when(magnet.getConnections()).thenReturn(list);
        WiresConnector connector = mock(WiresConnector.class);
        when(connection.getConnector()).thenReturn(connector);
        Group connectorGroup = mock(Group.class);
        when(connector.getGroup()).thenReturn(connectorGroup);
        WiresShapeControlUtils.moveShapeUpToParent(shape, parent);
        verify(parentContainer, times(1)).moveToTop(eq(shapeGroup));
        verify(parentContainer, never()).moveDown(anyObject());
        verify(parentContainer, never()).moveUp(anyObject());
        verify(parentContainer, never()).moveToBottom(anyObject());
        verify(connectorGroup, times(1)).moveToTop();
        verify(connectorGroup, never()).moveUp();
        verify(connectorGroup, never()).moveDown();
        verify(connectorGroup, never()).moveToBottom();
    }
}
