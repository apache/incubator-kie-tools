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
package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeControlImplTest extends AbstractWiresControlTest {

    private WiresShapeControlImpl tested;

    @Mock
    private AlignAndDistributeControl alignAndDistributeControl;

    @Mock
    private WiresMagnetsControl m_magnetsControl;

    @Mock
    private WiresDockingControl m_dockingAndControl;

    @Mock
    private WiresContainmentControl m_containmentControl;

    @Mock
    private WiresConnectorHandler connectorHandler;

    @Mock
    private WiresConnectorControl connectorControl;

    private IDirectionalMultiPointShape line;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    @Mock
    private WiresShape childWiresShape;

    @Mock
    private MagnetManager.Magnets magnets;

    @Mock
    private WiresMagnet magnet;

    @Mock
    private WiresConnection connection;

    @Mock
    private WiresConnector connector;

    @Mock
    private Group connectorGroup;

    @Mock
    private MultiPath head;

    @Mock
    private MultiPath tail;

    private NFastArrayList<WiresConnection> connections;

    private static final String CONNECTOR_UUID = "UUID";

    private static final Point2D CONTROL_POINT_LIENZO = new Point2D(100, 100);

    private static final Point2DArray CONTROL_POINTS_LIENZO = new Point2DArray(CONTROL_POINT_LIENZO);

    @Before
    public void setup() {
        super.setUp();
        connections = new NFastArrayList<>(connection);
        line = new PolyLine(0, 0, 10, 10, 100, 100);

        when(childWiresShape.getMagnets()).thenReturn(magnets);
        when(childWiresShape.getParent()).thenReturn(shape);
        when(magnets.size()).thenReturn(1);
        when(magnets.getMagnet(0)).thenReturn(magnet);
        when(magnet.getConnectionsSize()).thenReturn(connections.size());
        when(magnet.getConnections()).thenReturn(connections);
        when(connection.getConnector()).thenReturn(connector);
        when(connector.getGroup()).thenReturn(connectorGroup);
        when(connectorGroup.uuid()).thenReturn(CONNECTOR_UUID);
        when(connector.getControlPoints()).thenReturn(CONTROL_POINTS_LIENZO);
        when(connector.getWiresConnectorHandler()).thenReturn(connectorHandler);
        when(connectorHandler.getControl()).thenReturn(connectorControl);
        when(connector.getLine()).thenReturn(line);
        when(connector.getHeadDecorator()).thenReturn(headDecorator);
        when(connector.getTailDecorator()).thenReturn(tailDecorator);
        when(parentPicker.onMove(10, 10)).thenReturn(false);
        when(connector.getHead()).thenReturn(head);
        when(connector.getTail()).thenReturn(tail);
        when(head.getLocation()).thenReturn(new Point2D(1, 1));
        when(tail.getLocation()).thenReturn(new Point2D(2, 2));

        tested = new WiresShapeControlImpl(parentPicker, m_magnetsControl, m_dockingAndControl, m_containmentControl);
        tested.setAlignAndDistributeControl(alignAndDistributeControl);
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(parentPicker, times(1)).clear();
        verify(index, times(1)).clear();
        verify(m_dockingAndControl, times(1)).clear();
        verify(m_containmentControl, times(1)).clear();
    }

    @Test
    public void testOnMoveStart() {
        shape.getChildShapes().add(childWiresShape);
        tested.onMoveStart(2, 7);
        verify(index, never()).clear();
        verify(index, times(1)).addShapeToSkip(eq(shape));
        verify(index, times(1)).addShapeToSkip(eq(childWiresShape));
        verify(m_dockingAndControl, times(1)).onMoveStart(eq(2d), eq(7d));
        verify(m_containmentControl, times(1)).onMoveStart(eq(2d), eq(7d));
        verify(alignAndDistributeControl, times(1)).dragStart();
    }

    @Test
    public void testConnectionsMoveWithNoChildren() {

        tested.onMoveStart(0, 0);
        verify(connectorControl, never()).onMoveStart(anyDouble(), anyDouble());

        tested.onMove(10, 10);
        verify(connectorControl, never()).move(anyDouble(), anyDouble(), anyBoolean(), anyBoolean());
    }

    @Test
    public void testConnectionsMoveWithChildren() {
        shape.getChildShapes().add(childWiresShape);

        tested.onMoveStart(1, 1);
        verify(connectorControl).onMoveStart(1, 1);

        tested.onMove(10, 10);
        verify(connectorControl).move(10, 10, true, true);
    }
}