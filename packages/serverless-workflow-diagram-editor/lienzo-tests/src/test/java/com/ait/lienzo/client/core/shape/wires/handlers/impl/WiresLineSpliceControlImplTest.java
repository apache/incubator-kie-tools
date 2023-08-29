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

import java.util.List;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.ILineSpliceAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import elemental2.core.Uint8ClampedArray;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.ImageData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresLineSpliceControlImplTest {

    private WiresLineSpliceControlImpl tested;

    private static final double X = 1.1d;

    private static final double Y = 2.2d;

    private WiresShape spliceShape;

    private WiresShape sourceShape;

    private WiresShape targetShape;

    private WiresShape parent;

    private Layer layer;

    private WiresManager manager;

    private NFastArrayList<WiresConnector> connectors;

    private ILineSpliceAcceptor lineSpliceAcceptor;

    private PolyLine line;

    private PolyLine line2;

    private Point2D CP0;

    private Point2D CP1;

    private Point2D CP2;

    private Point2D CP3;

    @Mock
    private WiresParentPickerControlImpl parentPicker;

    @Mock
    private WiresColorMapIndex index;

    @Mock
    private WiresShapeControl spliceShapeControl;

    @Mock
    private WiresShapeControl sourceShapeControl;

    @Mock
    private WiresShapeControl targetShapeControl;

    @Mock
    private WiresMagnetsControl spliceShapeMagnetsControl;

    @Mock
    private WiresMagnetsControl sourceShapeMagnetsControl;

    @Mock
    private WiresMagnetsControl targetShapeMagnetsControl;

    @Mock
    private WiresShapeControl parentControl;

    @Mock
    private WiresMagnetsControl parentMagnetsControl;

    @Mock
    private HTMLDivElement div;

    @Mock
    private Viewport viewport;

    @Mock
    private Magnets sourceShapeMagnets;

    @Mock
    private Magnets targetShapeMagnets;

    @Mock
    private WiresConnector connector;

    @Mock
    private WiresConnection headConnection;

    @Mock
    private WiresConnection tailConnection;

    @Mock
    private WiresMagnet headMagnet;

    @Mock
    private WiresMagnet tailMagnet;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private Context2D context;

    @Mock
    private ImageData imageData;

    @Mock
    private WiresConnectorControl connectorControl;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator headDecoratorCopy;

    @Mock
    private MultiPath headMultiPath;

    @Mock
    private MultiPath headMultiPathCopy;

    @Mock
    private MultiPathDecorator tailDecorator;

    @Mock
    private MultiPathDecorator tailDecoratorCopy;

    @Mock
    private MultiPath tailMultiPath;

    @Mock
    private MultiPath tailMultiPathCopy;

    @Mock
    private IPrimitive headControl;

    @Mock
    private IPrimitive tailControl;

    @Mock
    private Group connectorGroup;

    @Mock
    private HandlerRegistration headMouseEnterHandlerRegistration;

    @Mock
    private HandlerRegistration headMouseMoveHandlerRegistration;

    @Mock
    private HandlerRegistration headMouseExitHandlerRegistration;

    @Mock
    private HandlerRegistration headMouseClickHandlerRegistration;

    @Mock
    private HandlerRegistration tailMouseEnterHandlerRegistration;

    @Mock
    private HandlerRegistration tailMouseMoveHandlerRegistration;

    @Mock
    private HandlerRegistration tailMouseExitHandlerRegistration;

    @Mock
    private HandlerRegistration tailMouseClickHandlerRegistration;

    @Mock
    private HandlerRegistration headCopyMouseEnterHandlerRegistration;

    @Mock
    private HandlerRegistration headCopyMouseMoveHandlerRegistration;

    @Mock
    private HandlerRegistration headCopyMouseExitHandlerRegistration;

    @Mock
    private HandlerRegistration headCopyMouseClickHandlerRegistration;

    @Mock
    private HandlerRegistration tailCopyMouseEnterHandlerRegistration;

    @Mock
    private HandlerRegistration tailCopyMouseMoveHandlerRegistration;

    @Mock
    private HandlerRegistration tailCopyMouseExitHandlerRegistration;

    @Mock
    private HandlerRegistration tailCopyMouseClickHandlerRegistration;

    @Before
    public void setUp() {
        connectors = new NFastArrayList<>();
        layer = spy(new Layer());
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(div);
        manager = spy(WiresManager.get(layer));
    }

    @After
    public void cleanUp() {
        MagnetManager.m_c_rotor.reset();
    }

    private void finishSetup() {
        manager.setLineSpliceAcceptor(lineSpliceAcceptor);

        when(spliceShapeControl.getMagnetsControl()).thenReturn(sourceShapeMagnetsControl);
        sourceShape = spy(new WiresShape(new MultiPath().rect(0, 50, 10, 10)));
        sourceShape.setWiresManager(manager);
        sourceShape.setControl(sourceShapeControl);

        when(spliceShapeControl.getMagnetsControl()).thenReturn(targetShapeMagnetsControl);
        targetShape = spy(new WiresShape(new MultiPath().rect(340, 50, 10, 10)));
        targetShape.setWiresManager(manager);
        targetShape.setControl(targetShapeControl);

        when(spliceShapeControl.getMagnetsControl()).thenReturn(spliceShapeMagnetsControl);
        spliceShape = spy(new WiresShape(new MultiPath().rect(120, 50, 10, 10)));
        spliceShape.setWiresManager(manager);
        spliceShape.setControl(spliceShapeControl);

        when(parentControl.getMagnetsControl()).thenReturn(parentMagnetsControl);
        parent = new WiresShape(new MultiPath().rect(50, 0, 50, 100));
        parent.setWiresManager(manager);
        parent.setControl(parentControl);
        manager.getMagnetManager().createMagnets(parent);

        when(parentPicker.getParent()).thenReturn(null);
        when(parentPicker.getParentShapePart()).thenReturn(PickerPart.ShapePart.BORDER);
        when(parentPicker.getShape()).thenReturn(spliceShape);
        when(parentPicker.getCurrentLocation()).thenReturn(parent.getLocation());
        when(parentPicker.onMove(anyDouble(), anyDouble())).thenReturn(true);
        when(parentPicker.getIndex()).thenReturn(index);
        when(parentPicker.getShapeLocation()).thenReturn(new Point2D(60, 0));

        parent.setLocation(new Point2D(0, 0));

        tested = spy(new WiresLineSpliceControlImpl(() -> parentPicker));
    }

    @Test
    public void testDestroy() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        tested.destroy();

        verify(lineSpliceAcceptor, times(1)).ensureUnHighLight();
        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
        assertNull(tested.m_wiresManager);
        assertNull(tested.m_lineSpliceAcceptor);
    }

    @Test
    public void testOnMoveSpliceDisabled() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(false);
        assertFalse(tested.onMove(X, Y));
        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
    }

    @Test
    public void testOnMoveNotAllowedNoMagnets() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        assertFalse(tested.onMove(X, Y));
        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
    }

    @Test
    public void testOnMoveNotAllowedInvalidConnections() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        setSpliceShapeMagnets();
        manager.setSpliceEnabled(true);

        // No connections
        assertFalse(tested.onMove(X, Y));

        setConnector(false);

        // spliceShape equals source
        when(parentPicker.getShape()).thenReturn(sourceShape);
        assertFalse(tested.onMove(X, Y));

        // spliceShape equals target
        when(parentPicker.getShape()).thenReturn(targetShape);
        assertFalse(tested.onMove(X, Y));

        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
    }

    @Test
    public void testOnMoveNotAllowedNoIntersection() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setSpliceShapeMagnets();
        setNoIntersectionPoints();
        setConnector(true);

        assertFalse(tested.onMove(X, Y));
        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
    }

    @Test
    public void testOnMoveNotAllowedSingleIntersection() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setSpliceShapeMagnets();
        setSingleIntersectionPoint();
        setConnector(true);

        assertFalse(tested.onMove(X, Y));
        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertEquals(1, tested.m_intersectPoints.size(), 0);
    }

    @Test
    public void testOnMoveNotAllowedNoFirstSegmentIndex() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataNoMatch();
        setSpliceShapeMagnets();
        setConnector(true);

        assertFalse(tested.onMove(X, Y));
        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertEquals(2, tested.m_intersectPoints.size(), 0);
    }

    @Test
    public void testOnMoveAcceptorAllowed() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();

        assertTrue(tested.onMove(X, Y));
        assertNotNull(tested.m_candidateConnector);
        assertEquals(2, tested.m_firstHalfPoints.size(), 0);
        assertEquals(2, tested.m_secondHalfPoints.size(), 0);
        assertEquals(2, tested.m_intersectPoints.size(), 0);
    }

    @Test
    public void testOnMoveAcceptorNotAllowed() {
        setLineSpliceAcceptorNotAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();

        assertFalse(tested.onMove(X, Y));
        assertNotNull(tested.m_candidateConnector);
        assertEquals(2, tested.m_firstHalfPoints.size(), 0);
        assertEquals(2, tested.m_secondHalfPoints.size(), 0);
        assertEquals(2, tested.m_intersectPoints.size(), 0);
    }

    @Test
    public void testAcceptSpliceDisabled() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);

        assertFalse(tested.accept());
    }

    @Test
    public void testAcceptTrue() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();

        tested.splice(false);

        assertTrue(tested.accept());
    }

    @Test
    public void testAcceptFalse() {
        setLineSpliceAcceptorNotAccept();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();

        tested.splice(false);

        assertFalse(tested.accept());
    }

    @Test
    public void testGetAdjust() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        final Point2D adjust = tested.getAdjust();

        assertEquals(0, adjust.getX(), 0);
        assertEquals(0, adjust.getY(), 0);
    }

    @Test
    public void testReset() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();

        tested.splice(false);

        assertNotNull(tested.m_candidateConnector);
        assertEquals(2, tested.m_firstHalfPoints.size(), 0);
        assertEquals(2, tested.m_secondHalfPoints.size(), 0);
        assertEquals(2, tested.m_intersectPoints.size(), 0);

        tested.clear();

        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
    }

    @Test
    public void testExecuteSpliceDisabled() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(false);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();

        tested.execute();

        assertNull(tested.m_candidateConnector);
        assertNull(tested.m_firstHalfPoints);
        assertNull(tested.m_secondHalfPoints);
        assertNull(tested.m_intersectPoints);
    }

    @Test
    public void testExecuteSpliceWithoutContainment() {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();
        setNewConnectorHandlers();

        // Check containment before
        assertEquals(0, parent.getChildShapes().size(), 0);

        tested.execute();

        assertNotNull(tested.m_candidateConnector);
        // One extra point is added at the end to connect to the splicing shape
        assertEquals(3, tested.m_firstHalfPoints.size(), 0);
        // One extra point is added at the beginning to connect to the splicing shape
        assertEquals(3, tested.m_secondHalfPoints.size(), 0);
        assertEquals(2, tested.m_intersectPoints.size(), 0);

        // Must have two connectors after splicing
        assertEquals(2, connectors.getLength(), 0);
        assertNotEquals(connectors.get(0), connectors.get(1));

        // Cloned connector
        assertEquals(connectors.get(0).getHead(), headMultiPathCopy);
        assertEquals(connectors.get(0).getTail(), tailMultiPathCopy);
        assertEquals(3, connectors.get(0).getLine().getPoint2DArray().getLength(), 0);

        // Original connector
        assertEquals(connectors.get(1).getHead(), headMultiPath);
        assertEquals(connectors.get(1).getTail(), tailMultiPath);
        assertEquals(3, connectors.get(1).getLine().getPoint2DArray().getLength(), 0);

        // the when changing CP the connector needs to be registered again
        verify(manager, times(1)).deregister(connector);
        verify(manager, times(1)).register(connector);

        // Registration of the changed and new connector
        verify(manager, times(2)).register(any(WiresConnector.class));

        // Check containment after
        assertEquals(0, parent.getChildShapes().size(), 0);
    }

    @Test
    public void testExecuteSpliceWithContainmentWithoutDeviation() {
        checkSpliceAndContainment(false);
    }

    @Test
    public void testExecuteSpliceWithContainmentWithDeviation() {
        checkSpliceAndContainment(true);
    }

    private void checkSpliceAndContainment(final boolean deviation) {
        setLineSpliceAcceptorAllowed();
        finishSetup();
        manager.setSpliceEnabled(true);
        setMultipleIntersectionPoints();
        setImageDataMatch();
        setConnector(true);
        setSpliceShapeMagnets();
        setNewConnectorHandlers();

        if (deviation) {
            final WiresLayer wiresLayer = manager.getLayer();
            when(parentPicker.getParent()).thenReturn(wiresLayer);
            PickerPart pickerPart = mock(PickerPart.class);
            when(pickerPart.getShape()).thenReturn(parent);
            when(index.findShapeAt(WiresLineSpliceControlImpl.XY_OFFSET,
                                   -WiresLineSpliceControlImpl.XY_OFFSET)).thenReturn(pickerPart);
        } else {
            when(parentPicker.getParent()).thenReturn(parent);
        }

        // Check containment before
        assertEquals(0, parent.getChildShapes().size(), 0);

        tested.execute();

        assertNotNull(tested.m_candidateConnector);
        // One extra point is added at the end to connect to the splicing shape
        assertEquals(3, tested.m_firstHalfPoints.size(), 0);
        // One extra point is added at the beginning to connect to the splicing shape
        assertEquals(3, tested.m_secondHalfPoints.size(), 0);
        assertEquals(2, tested.m_intersectPoints.size(), 0);

        // Must have two connectors after splicing
        assertEquals(2, connectors.getLength(), 0);
        assertNotEquals(connectors.get(0), connectors.get(1));

        // Cloned connector
        assertEquals(connectors.get(0).getHead(), headMultiPathCopy);
        assertEquals(connectors.get(0).getTail(), tailMultiPathCopy);
        assertEquals(3, connectors.get(0).getLine().getPoint2DArray().getLength(), 0);

        // Original connector
        assertEquals(connectors.get(1).getHead(), headMultiPath);
        assertEquals(connectors.get(1).getTail(), tailMultiPath);
        assertEquals(3, connectors.get(1).getLine().getPoint2DArray().getLength(), 0);

        // the when changing CP the connector needs to be registered again
        verify(manager, times(1)).deregister(connector);
        verify(manager, times(1)).register(connector);

        // Registration of the changed and new connector
        verify(manager, times(2)).register(any(WiresConnector.class));

        // Check containment after
        assertEquals(1, parent.getChildShapes().size(), 0);
        verify(parentPicker, times(1)).getParent();

        if (deviation) {
            verify(index, times(1)).findShapeAt(WiresLineSpliceControlImpl.XY_OFFSET,
                                                -WiresLineSpliceControlImpl.XY_OFFSET);
        } else {
            verify(index, never()).findShapeAt(WiresLineSpliceControlImpl.XY_OFFSET,
                                               -WiresLineSpliceControlImpl.XY_OFFSET);
        }
    }

    private void setSpliceShapeMagnets() {
        manager.getMagnetManager().createMagnets(spliceShape);
    }

    private void setConnector(final boolean runParse) {
        if (null == CP2 && null == CP3) {
            line = spy(new PolyLine(CP0, CP1));
        } else {
            line = spy(new PolyLine(CP0, CP1, CP2, CP3));
            line2 = spy(new PolyLine(CP0.copy(), CP1.copy(), CP2.copy(), CP3.copy()));

            if (runParse) {
                line.parse();
                line2.parse();
            }

            when(line.cloneLine()).thenReturn(line2);
        }

        when(line.getScratchPad()).thenReturn(scratchPad);
        when(scratchPad.getContext()).thenReturn(context);
        when(headControl.getComputedLocation()).thenReturn(CP0);
        when(tailControl.getComputedLocation()).thenReturn(null != CP3 ? CP3 : CP1);
        when(tailMagnet.getControl()).thenReturn(tailControl);
        when(headMagnet.getControl()).thenReturn(headControl);
        when(tailMagnet.getControl()).thenReturn(tailControl);
        when(headConnection.getMagnet()).thenReturn(headMagnet);
        when(tailConnection.getMagnet()).thenReturn(tailMagnet);
        when(connector.getHeadConnection()).thenReturn(headConnection);
        when(connector.getTailConnection()).thenReturn(tailConnection);
        when(connector.getHead()).thenReturn(headMultiPath);
        when(connector.getTail()).thenReturn(tailMultiPath);
        when(connector.getGroup()).thenReturn(connectorGroup);
        when(headMultiPath.asNode()).thenReturn(mock(Node.class));
        when(tailMultiPath.asNode()).thenReturn(mock(Node.class));
        when(headDecorator.getPath()).thenReturn(headMultiPath);
        when(tailDecorator.getPath()).thenReturn(tailMultiPath);
        when(headMultiPathCopy.asNode()).thenReturn(mock(Node.class));
        when(tailMultiPathCopy.asNode()).thenReturn(mock(Node.class));
        when(headDecoratorCopy.getPath()).thenReturn(headMultiPathCopy);
        when(tailDecoratorCopy.getPath()).thenReturn(tailMultiPathCopy);
        when(headDecorator.copy()).thenReturn(headDecoratorCopy);
        when(tailDecorator.copy()).thenReturn(tailDecoratorCopy);
        ;
        when(connector.getHeadDecorator()).thenReturn(headDecorator);
        when(connector.getTailDecorator()).thenReturn(tailDecorator);
        when(headConnection.getMagnet()).thenReturn(headMagnet);
        when(tailConnection.getMagnet()).thenReturn(tailMagnet);
        when(headConnection.getConnector()).thenReturn(connector);
        when(tailConnection.getConnector()).thenReturn(connector);
        when(headMagnet.getMagnets()).thenReturn(sourceShapeMagnets);
        when(tailMagnet.getMagnets()).thenReturn(targetShapeMagnets);
        when(sourceShapeMagnets.getWiresShape()).thenReturn(sourceShape);
        when(targetShapeMagnets.getWiresShape()).thenReturn(targetShape);
        when(connector.getLine()).thenReturn((IDirectionalMultiPointShape) line);
        when(connector.getControl()).thenReturn(connectorControl);

        connectors.add(connector);
        when(manager.getConnectorList()).thenReturn(connectors);
    }

    private void setNewConnectorHandlers() {
        when(headMultiPath.addNodeMouseEnterHandler(any(WiresConnectorHandler.class))).thenReturn(headMouseEnterHandlerRegistration);
        when(headMultiPath.addNodeMouseMoveHandler(any(WiresConnectorHandler.class))).thenReturn(headMouseMoveHandlerRegistration);
        when(headMultiPath.addNodeMouseExitHandler(any(WiresConnectorHandler.class))).thenReturn(headMouseExitHandlerRegistration);
        when(headMultiPath.addNodeMouseClickHandler(any(WiresConnectorHandler.class))).thenReturn(headMouseClickHandlerRegistration);

        when(tailMultiPath.addNodeMouseEnterHandler(any(WiresConnectorHandler.class))).thenReturn(tailMouseEnterHandlerRegistration);
        when(tailMultiPath.addNodeMouseMoveHandler(any(WiresConnectorHandler.class))).thenReturn(tailMouseMoveHandlerRegistration);
        when(tailMultiPath.addNodeMouseExitHandler(any(WiresConnectorHandler.class))).thenReturn(tailMouseExitHandlerRegistration);
        when(tailMultiPath.addNodeMouseClickHandler(any(WiresConnectorHandler.class))).thenReturn(tailMouseClickHandlerRegistration);

        when(headMultiPathCopy.addNodeMouseEnterHandler(any(WiresConnectorHandler.class))).thenReturn(headCopyMouseEnterHandlerRegistration);
        when(headMultiPathCopy.addNodeMouseMoveHandler(any(WiresConnectorHandler.class))).thenReturn(headCopyMouseMoveHandlerRegistration);
        when(headMultiPathCopy.addNodeMouseExitHandler(any(WiresConnectorHandler.class))).thenReturn(headCopyMouseExitHandlerRegistration);
        when(headMultiPathCopy.addNodeMouseClickHandler(any(WiresConnectorHandler.class))).thenReturn(headCopyMouseClickHandlerRegistration);

        when(tailMultiPathCopy.addNodeMouseEnterHandler(any(WiresConnectorHandler.class))).thenReturn(tailCopyMouseEnterHandlerRegistration);
        when(tailMultiPathCopy.addNodeMouseMoveHandler(any(WiresConnectorHandler.class))).thenReturn(tailCopyMouseMoveHandlerRegistration);
        when(tailMultiPathCopy.addNodeMouseExitHandler(any(WiresConnectorHandler.class))).thenReturn(tailCopyMouseExitHandlerRegistration);
        when(tailMultiPathCopy.addNodeMouseClickHandler(any(WiresConnectorHandler.class))).thenReturn(tailCopyMouseClickHandlerRegistration);
    }

    private void setImageDataMatch() {
        Uint8ClampedArray data = mock(Uint8ClampedArray.class);
        imageData.data = data;

        when(data.getAt(444)).thenReturn(64.0);
        when(data.getAt(445)).thenReturn(0.0);
        when(data.getAt(446)).thenReturn(0.0);
        when(data.getAt(447)).thenReturn(255.0);

        when(data.getAt(484)).thenReturn(32.0);
        when(data.getAt(485)).thenReturn(0.0);
        when(data.getAt(486)).thenReturn(0.0);
        when(data.getAt(487)).thenReturn(255.0);

        when(context.getImageData(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(imageData);
    }

    private void setImageDataNoMatch() {
        Uint8ClampedArray data = mock(Uint8ClampedArray.class);
        imageData.data = data;

        when(data.getAt(444)).thenReturn(16.0);
        when(data.getAt(445)).thenReturn(0.0);
        when(data.getAt(446)).thenReturn(0.0);
        when(data.getAt(447)).thenReturn(255.0);

        when(data.getAt(484)).thenReturn(32.0);
        when(data.getAt(485)).thenReturn(0.0);
        when(data.getAt(486)).thenReturn(0.0);
        when(data.getAt(487)).thenReturn(255.0);

        when(context.getImageData(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(imageData);
    }

    private void setNoIntersectionPoints() {
        CP0 = new Point2D(10, 100);
        CP1 = new Point2D(50, 100);
        CP2 = new Point2D(90, 100);
        CP3 = new Point2D(130, 100);
    }

    private void setSingleIntersectionPoint() {
        CP0 = new Point2D(10, 50);
        CP1 = new Point2D(120, 50);
        CP2 = null;
        CP3 = null;
    }

    private void setMultipleIntersectionPoints() {
        CP0 = new Point2D(10, 50);
        CP1 = new Point2D(200, 50);
        CP2 = new Point2D(230, 50);
        CP3 = new Point2D(340, 50);
    }

    private void setLineSpliceAcceptorNotAllowed() {
        lineSpliceAcceptor = spy(ILineSpliceAcceptor.NONE);
    }

    private void setLineSpliceAcceptorAllowed() {
        lineSpliceAcceptor = spy(ILineSpliceAcceptor.ALL);
    }

    private void setLineSpliceAcceptorNotAccept() {
        lineSpliceAcceptor = spy(new ILineSpliceAcceptor() {
            @Override
            public boolean allowSplice(WiresShape shape,
                                       double[] candidateLocation,
                                       WiresConnector connector,
                                       WiresContainer parent) {
                return true;
            }

            @Override
            public boolean acceptSplice(WiresShape shape,
                                        double[] candidateLocation,
                                        WiresConnector connector,
                                        List<double[]> firstHalfPoints,
                                        List<double[]> secondHalfPoints,
                                        WiresContainer parent) {
                return false;
            }

            @Override
            public void ensureUnHighLight() {
            }
        });
    }
}