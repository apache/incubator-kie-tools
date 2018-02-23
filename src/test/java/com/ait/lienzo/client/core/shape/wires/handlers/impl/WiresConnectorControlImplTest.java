/*
 *
 *    Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.Iterator;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorControlImplTest {

    private WiresConnectorControl wiresConnectorControl;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresConnector connector;

    @Mock
    private AbstractDirectionalMultiPointShape line;

    @Mock
    private Shape<?> lineShape;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private PathPartList pathPartList;

    @Mock
    private Context2D context;

    @Mock
    private Point2D location;

    @Mock
    private BoundingBox boundingBox;

    @Mock
    private ImageData backImage;

    @Mock
    private IControlHandleList pointHandles;

    @Mock
    private HandlerRegistrationManager handlerRegistration;

    @Mock
    private WiresControlFactory controlFactory;

    @Mock
    private WiresConnectorControl connectorControl;

    @Mock
    private WiresConnection headConnection;

    @Mock
    private IPrimitive headControl;

    @Mock
    private IPrimitive pointControl;

    @Mock
    private Shape headShape;

    @Mock
    private WiresConnection tailConnection;

    @Mock
    private IPrimitive tailControl;

    @Mock
    private Shape tailShape;

    @Mock
    private Shape pointShape;

    @Mock
    private WiresHandlerFactory wiresHandlerFactory;

    @Mock
    private WiresControlPointHandler connectorControlHandler;

    private Point2D head;

    private Point2D tail;

    private Point2DArray lineArray;

    @Mock
    private Iterator<IControlHandle> pointHandlesIterator;

    @Mock
    private IControlHandle pointHandle;

    @Mock
    private Layer layer;

    @Mock
    private HandlerRegistration controlPointRegistration;

    @Before
    public void setup() {
        wiresConnectorControl = new WiresConnectorControlImpl(connector, wiresManager);

        head = new Point2D(0, 0);
        tail = new Point2D(100, 0);
        lineArray = new Point2DArray(head, tail);

        when(line.getPoint2DArray()).thenReturn(lineArray);
        when(line.getScratchPad()).thenReturn(scratchPad);
        when(line.getPathPartList()).thenReturn(pathPartList);
        when(line.getComputedLocation()).thenReturn(location);
        when(line.getBoundingBox()).thenReturn(boundingBox);
        when(line.asShape()).thenReturn(lineShape);
        when(lineShape.getPathPartList()).thenReturn(pathPartList);
        when(connector.getLine()).thenReturn(line);
        when(scratchPad.getContext()).thenReturn(context);
        when(context.getImageData(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(backImage);
        when(connector.getPointHandles()).thenReturn(pointHandles);
        when(pointHandles.getHandlerRegistrationManager()).thenReturn(handlerRegistration);
        when(wiresManager.getControlFactory()).thenReturn(controlFactory);
        when(wiresManager.getWiresHandlerFactory()).thenReturn(wiresHandlerFactory);
        when(controlFactory.newConnectorControl(connector, wiresManager)).thenReturn(connectorControl);
        when(connector.getHeadConnection()).thenReturn(headConnection);
        when(headConnection.getControl()).thenReturn(headControl);
        when(headControl.asShape()).thenReturn(headShape);
        when(pointControl.asShape()).thenReturn(pointShape);
        when(connector.getTailConnection()).thenReturn(tailConnection);
        when(tailConnection.getControl()).thenReturn(tailControl);
        when(tailControl.asShape()).thenReturn(tailShape);
        when(wiresHandlerFactory.newControlPointHandler(connector, wiresConnectorControl)).thenReturn(connectorControlHandler);
        when(line.getLayer()).thenReturn(layer);
        when(pointHandles.iterator()).thenReturn(pointHandlesIterator);
        when(pointHandlesIterator.hasNext()).thenReturn(true, true, true, false);
        when(pointHandlesIterator.next()).thenReturn(pointHandle);
        when(pointHandle.getControl()).thenReturn(headControl, pointControl, tailControl);
        when(pointShape.addNodeMouseDoubleClickHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(pointShape.addNodeDragStartHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(pointShape.addNodeDragEndHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(pointShape.addNodeDragMoveHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(headShape.addNodeMouseDoubleClickHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(headShape.addNodeDragStartHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(headShape.addNodeDragEndHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(headShape.addNodeDragMoveHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(tailShape.addNodeMouseDoubleClickHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(tailShape.addNodeDragStartHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(tailShape.addNodeDragEndHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
        when(tailShape.addNodeDragMoveHandler(connectorControlHandler)).thenReturn(controlPointRegistration);
    }

    @Test
    public void testAddControlPointToLineAndRemove() {
        final double x = 10;
        final double y = 0;
        final int index = 1;

        ArgumentCaptor<Point2DArray> linePoint2DArrayArgumentCaptor = ArgumentCaptor.forClass(Point2DArray.class);
        ArgumentCaptor<Node> nodeArgumentCaptor= ArgumentCaptor.forClass(Node.class);

        //add
        wiresConnectorControl.addControlPointToLine(x, y, index);
        verify(line, times(1)).setPoint2DArray(linePoint2DArrayArgumentCaptor.capture());
        verify(pointShape).addNodeMouseDoubleClickHandler(connectorControlHandler);
        verify(pointShape).addNodeDragStartHandler(connectorControlHandler);
        verify(pointShape).addNodeDragEndHandler(connectorControlHandler);
        verify(pointShape).addNodeDragMoveHandler(connectorControlHandler);
        //4 times for each point (head, point, tail)
        verify(handlerRegistration, times(3 * 4)).register(controlPointRegistration);
        Point2D point2D = linePoint2DArrayArgumentCaptor.getValue().get(index);
        assertEquals(point2D.getX(), x, 0);
        assertEquals(point2D.getY(), y, 0);
        assertEquals(linePoint2DArrayArgumentCaptor.getValue().size(), 3);

        //remove
        wiresConnectorControl.removeControlPoint(x, y);
        verify(line, times(2)).setPoint2DArray(linePoint2DArrayArgumentCaptor.capture());
        Point2DArray newLineArray = linePoint2DArrayArgumentCaptor.getValue();
        assertEquals(newLineArray.size(), 2);
        assertEquals(newLineArray.get(0), head);
        assertEquals(newLineArray.get(1), tail);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddControlPointToLineInvalidIndex() {
        wiresConnectorControl.addControlPointToLine(10, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddControlPointToLineInvalidIndex2() {
        wiresConnectorControl.addControlPointToLine(10, 0, lineArray.size());
    }
}