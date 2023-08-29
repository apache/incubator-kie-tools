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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLineSpliceControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeControlImplTest extends AbstractWiresControlTest {

    private static final String CONNECTOR_UUID = "UUID";
    private static final Point2D CONTROL_POINT_LIENZO = new Point2D(100, 100);
    private static final Point2DArray CONTROL_POINTS_LIENZO = Point2DArray.fromArrayOfPoint2D(CONTROL_POINT_LIENZO);

    @Mock
    private ILocationAcceptor locationAcceptor;

    @Mock
    private AlignAndDistributeControl alignAndDistributeControl;

    @Mock
    private WiresMagnetsControl m_magnetsControl;

    @Mock
    private WiresDockingControl m_dockingAndControl;

    @Mock
    private WiresContainmentControl m_containmentControl;

    @Mock
    private WiresLineSpliceControl m_lineSpliceControl;

    @Mock
    private WiresConnectorControl connectorControl;

    private IDirectionalMultiPointShape line;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    @Mock
    private WiresShape childWiresShape1;

    @Mock
    private MagnetManager.Magnets magnets1;

    @Mock
    private WiresMagnet magnet1;

    @Mock
    private WiresConnection connection1;

    @Mock
    private WiresShape childWiresShape2;

    @Mock
    private MagnetManager.Magnets magnets2;

    @Mock
    private WiresMagnet magnet2;

    @Mock
    private WiresConnection connection2;

    @Mock
    private WiresConnector connector;

    @Mock
    private Group connectorGroup;

    @Mock
    private MultiPath head;

    @Mock
    private MultiPath tail;

    private WiresShapeControlImpl tested;
    private NFastArrayList<WiresConnection> connections;

    @Before
    public void setup() {
        super.setUp();
        manager.setLocationAcceptor(locationAcceptor);
        connections = new NFastArrayList<>();
        connections.add(connection1);
        connections.add(connection2);
        line = new PolyLine(0, 0, 10, 10, 100, 100);
        shape.getChildShapes().add(childWiresShape1);
        shape.getChildShapes().add(childWiresShape2);

        when(childWiresShape1.getMagnets()).thenReturn(magnets1);
        when(childWiresShape1.getParent()).thenReturn(shape);
        when(magnets1.size()).thenReturn(1);
        when(magnets1.getMagnet(0)).thenReturn(magnet1);
        when(magnets1.getWiresShape()).thenReturn(childWiresShape1);
        when(magnet1.getConnectionsSize()).thenReturn(connections.size());
        when(magnet1.getConnections()).thenReturn(connections);
        when(magnet1.getMagnets()).thenReturn(magnets1);
        when(connection1.getConnector()).thenReturn(connector);
        when(connection1.getOppositeConnection()).thenReturn(connection2);
        when(connection1.getMagnet()).thenReturn(magnet1);

        when(childWiresShape2.getMagnets()).thenReturn(magnets2);
        when(childWiresShape2.getParent()).thenReturn(shape);
        when(magnets2.size()).thenReturn(1);
        when(magnets2.getMagnet(0)).thenReturn(magnet2);
        when(magnets2.getWiresShape()).thenReturn(childWiresShape2);
        when(magnet2.getMagnets()).thenReturn(magnets2);
        when(magnet2.getConnectionsSize()).thenReturn(connections.size());
        when(magnet2.getConnections()).thenReturn(connections);
        when(connection2.getConnector()).thenReturn(connector);
        when(connection2.getOppositeConnection()).thenReturn(connection1);
        when(connection2.getMagnet()).thenReturn(magnet2);

        when(connector.getGroup()).thenReturn(connectorGroup);
        when(connectorGroup.uuid()).thenReturn(CONNECTOR_UUID);
        when(connector.getControlPoints()).thenReturn(CONTROL_POINTS_LIENZO);
        when(connector.getControl()).thenReturn(connectorControl);
        when(connector.getLine()).thenReturn(line);
        when(connector.getHeadDecorator()).thenReturn(headDecorator);
        when(connector.getTailDecorator()).thenReturn(tailDecorator);
        when(connector.getHeadConnection()).thenReturn(connection1);
        when(connector.getTailConnection()).thenReturn(connection2);
        when(parentPicker.getShape()).thenReturn(shape);
        when(parentPicker.onMove(10, 10)).thenReturn(false);
        when(connector.getHead()).thenReturn(head);
        when(connector.getTail()).thenReturn(tail);
        when(head.getLocation()).thenReturn(new Point2D(1, 1));
        when(tail.getLocation()).thenReturn(new Point2D(2, 2));

        tested = new WiresShapeControlImpl(parentPicker, m_magnetsControl,
                                           m_dockingAndControl,
                                           m_containmentControl,
                                           m_lineSpliceControl);
        tested.setAlignAndDistributeControl(alignAndDistributeControl);
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(parentPicker, times(1)).clear();
        verify(index, times(1)).clear();
        verify(m_dockingAndControl, times(1)).clear();
        verify(m_containmentControl, times(1)).clear();
        verify(m_lineSpliceControl, times(1)).clear();
    }

    @Test
    public void testOnMoveStart() {
        tested.onMoveStart(2, 7);
        verify(m_dockingAndControl, times(1)).onMoveStart(eq(2d), eq(7d));
        verify(m_containmentControl, times(1)).onMoveStart(eq(2d), eq(7d));
        verify(m_lineSpliceControl, times(1)).onMoveStart(eq(2d), eq(7d));
        verify(alignAndDistributeControl, times(1)).dragStart();
        verify(connectorControl, times(1)).onMoveStart(eq(2d), eq(7d));
        // Verify index is never updated.
        verify(index, never()).build(any(WiresLayer.class));
        verify(index, never()).exclude(any(WiresContainer.class));
        verify(index, never()).clear();
    }

    @Test
    public void testMoveShapeTopToParentOnMoveStart() {
        WiresContainer parent = mock(WiresContainer.class);
        IContainer container = mock(IContainer.class);
        when(parentPicker.getParent()).thenReturn(parent);
        when(parent.getContainer()).thenReturn(container);

        tested.onMoveStart(2, 7);

        verify(alignAndDistributeControl, times(1)).dragStart();
        verify(parentPicker, times(1)).getParent();
        verify(container, times(1)).moveToTop(shape.getGroup());
    }

    @Test
    public void testOnMove() {
        when(parentPicker.onMove(anyDouble(), anyDouble())).thenReturn(false);
        when(m_dockingAndControl.onMove(anyDouble(), anyDouble())).thenReturn(false);
        when(m_containmentControl.onMove(anyDouble(), anyDouble())).thenReturn(false);
        when(m_lineSpliceControl.onMove(anyDouble(), anyDouble())).thenReturn(false);
        tested.onMoveStart(1, 4);
        tested.onMove(2, 7);
        verify(m_dockingAndControl, times(1)).onMove(eq(2d), eq(7d));
        verify(m_containmentControl, times(1)).onMove(eq(2d), eq(7d));
        verify(m_lineSpliceControl, times(1)).onMove(eq(2d), eq(7d));
        verify(alignAndDistributeControl, times(1)).dragAdjust(eq(new Point2D(2, 7)));
        verify(connectorControl, times(1)).onMove(eq(2d), eq(7d));
    }

    @Test
    public void testOnMoveComplete() {
        tested.onMoveStart(1, 4);
        tested.onMoveComplete();
        verify(m_dockingAndControl, times(1)).onMoveComplete();
        verify(m_containmentControl, times(1)).onMoveComplete();
        verify(m_lineSpliceControl, times(1)).onMoveComplete();
        verify(alignAndDistributeControl, times(1)).dragEnd();
        verify(connectorControl, times(1)).onMoveComplete();
    }

    @Test
    public void testAcceptContainment() {
        Point2D somePoint = new Point2D(1, 2);
        when(locationAcceptor.accept(eq(new WiresContainer[]{shape}),
                                     eq(new Point2D[]{somePoint})))
                .thenReturn(true);
        when(m_containmentControl.accept()).thenReturn(true);
        when(m_dockingAndControl.accept()).thenReturn(false);
        when(m_lineSpliceControl.accept()).thenReturn(false);
        when(m_containmentControl.getCandidateLocation()).thenReturn(somePoint);
        when(m_dockingAndControl.getCandidateLocation()).thenReturn(null);
        boolean result = tested.accept();
        assertTrue(result);
    }

    @Test
    public void testAcceptDocking() {
        Point2D somePoint = new Point2D(1, 2);
        when(locationAcceptor.accept(eq(new WiresContainer[]{shape}),
                                     eq(new Point2D[]{somePoint})))
                .thenReturn(true);
        when(m_containmentControl.accept()).thenReturn(false);
        when(m_dockingAndControl.accept()).thenReturn(true);
        when(m_lineSpliceControl.accept()).thenReturn(false);
        when(m_containmentControl.getCandidateLocation()).thenReturn(null);
        when(m_dockingAndControl.getCandidateLocation()).thenReturn(somePoint);
        boolean result = tested.accept();
        assertTrue(result);
    }

    @Test
    public void testAcceptJustSplicing() {
        Point2D somePoint = new Point2D(1, 2);
        when(locationAcceptor.accept(eq(new WiresContainer[]{shape}),
                                     eq(new Point2D[]{somePoint})))
                .thenReturn(true);
        when(m_containmentControl.accept()).thenReturn(false);
        when(m_dockingAndControl.accept()).thenReturn(false);
        when(m_lineSpliceControl.accept()).thenReturn(true);
        when(m_containmentControl.getCandidateLocation()).thenReturn(null);
        when(m_dockingAndControl.getCandidateLocation()).thenReturn(somePoint);
        boolean result = tested.accept();
        assertFalse(result);
    }

    @Test
    public void testAcceptFailed() {
        when(m_containmentControl.accept()).thenReturn(false);
        when(m_dockingAndControl.accept()).thenReturn(false);
        boolean result = tested.accept();
        assertFalse(result);
    }

    @Test
    public void testExecuteContainment() {
        Point2D somePoint = new Point2D(1, 2);
        when(m_containmentControl.accept()).thenReturn(true);
        when(m_containmentControl.getCandidateLocation()).thenReturn(somePoint);
        when(locationAcceptor.accept(any(WiresContainer[].class),
                                     any(Point2D[].class)))
                .thenReturn(true);
        tested.onMoveStart(1, 2);
        tested.accept();
        tested.execute();
        verify(m_containmentControl, times(1)).execute();
        verify(parentPicker, times(1)).setShapeLocation(eq(somePoint));
        verify(connectorControl, times(1)).execute();
    }

    @Test
    public void testExecuteContainmentButNoLocation() {
        when(m_containmentControl.accept()).thenReturn(true);
        when(m_containmentControl.getCandidateLocation()).thenReturn(null);
        when(locationAcceptor.accept(any(WiresContainer[].class),
                                     any(Point2D[].class)))
                .thenReturn(true);
        tested.onMoveStart(1, 2);
        tested.accept();
        tested.execute();
        verify(parentPicker, never()).setShapeLocation(any(Point2D.class));
        verify(m_containmentControl, times(1)).execute();
        verify(connectorControl, times(1)).execute();
    }

    @Test
    public void testExecuteDocking() {
        Point2D somePoint = new Point2D(1, 2);
        when(m_dockingAndControl.accept()).thenReturn(true);
        when(m_dockingAndControl.getCandidateLocation()).thenReturn(somePoint);
        when(locationAcceptor.accept(any(WiresContainer[].class),
                                     any(Point2D[].class)))
                .thenReturn(true);
        tested.onMoveStart(2, 3);
        tested.accept();
        tested.execute();
        verify(m_dockingAndControl, times(1)).execute();
        verify(parentPicker, times(1)).setShapeLocation(eq(somePoint));
        verify(connectorControl, times(1)).execute();
    }

    @Test
    public void testExecuteDockingButNoLocation() {
        when(m_dockingAndControl.accept()).thenReturn(true);
        when(m_dockingAndControl.getCandidateLocation()).thenReturn(null);
        when(locationAcceptor.accept(any(WiresContainer[].class),
                                     any(Point2D[].class)))
                .thenReturn(true);
        tested.onMoveStart(2, 3);
        tested.accept();
        tested.execute();
        verify(parentPicker, never()).setShapeLocation(any(Point2D.class));
        verify(m_dockingAndControl, times(1)).execute();
        verify(connectorControl, times(1)).execute();
    }

    @Test
    public void testExecuteAllControlsAccepted() {
        when(m_lineSpliceControl.accept()).thenReturn(true);
        when(m_dockingAndControl.accept()).thenReturn(true);
        when(m_containmentControl.accept()).thenReturn(true);
        tested.onMoveStart(2, 3);
        tested.accept();
        tested.execute();

        verify(m_lineSpliceControl, times(1)).execute();
        verify(m_dockingAndControl, times(0)).execute();
        verify(m_containmentControl, times(0)).execute();
        verify(connectorControl, times(1)).execute();
    }

    @Test
    public void testExecuteOnlySplicingAccepted() {
        when(m_lineSpliceControl.accept()).thenReturn(true);
        when(m_dockingAndControl.accept()).thenReturn(false);
        when(m_containmentControl.accept()).thenReturn(false);
        tested.onMoveStart(2, 3);
        tested.accept();
        tested.execute();

        verify(m_lineSpliceControl, times(1)).execute();
        verify(m_dockingAndControl, times(0)).execute();
        verify(m_containmentControl, times(0)).execute();
        verify(connectorControl, times(1)).execute();
    }

    @Test
    public void testReset() {
        tested.onMoveStart(2, 3);
        tested.reset();
        assertEquals(new Point2D(0, 0), tested.getAdjust());
        verify(m_containmentControl, times(1)).reset();
        verify(m_dockingAndControl, times(1)).reset();
        verify(m_lineSpliceControl, times(1)).reset();
        verify(parentPicker, times(1)).reset();
        verify(connectorControl, times(1)).reset();
    }

    @Test
    public void testConnectionsMoveWithChildren() {

        tested.onMoveStart(1, 1);
        verify(connectorControl).onMoveStart(1, 1);

        tested.onMove(10, 10);
        verify(connectorControl).onMove(10, 10);
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertEquals(tested.getAdjust(), new Point2D(0d, 0d));
        verify(m_dockingAndControl).destroy();
        verify(m_containmentControl).destroy();
        verify(m_lineSpliceControl).destroy();
        verify(parentPicker).destroy();
        verify(alignAndDistributeControl).dragEnd();
    }

    @Test
    public void testConnectionsMoveWithNoChildren() {
        shape.getChildShapes().remove(childWiresShape1);
        shape.getChildShapes().remove(childWiresShape2);

        tested.onMoveStart(0, 0);
        verify(connectorControl, never()).onMoveStart(anyDouble(), anyDouble());

        tested.onMove(10, 10);
        verify(connectorControl, never()).onMove(anyDouble(), anyDouble());
    }

    @Test
    public void testBounds() {
        OptionalBounds bounds = OptionalBounds.create(0d, 0d, 1200d, 1200d);
        tested.setLocationBounds(bounds);
        tested.onMoveStart(0d, 0d);
        assertFalse(tested.isOutOfBounds(0.1d, 0.1d));
        assertTrue(tested.isOutOfBounds(-3d, -3d));
        assertFalse(tested.isOutOfBounds(800d, 800d));
        assertTrue(tested.isOutOfBounds(1191d, 1191d));
    }
}