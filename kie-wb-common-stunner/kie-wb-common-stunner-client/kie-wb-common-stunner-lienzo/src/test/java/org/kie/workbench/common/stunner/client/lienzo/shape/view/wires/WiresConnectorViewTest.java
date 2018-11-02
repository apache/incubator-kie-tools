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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.List;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.ControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.shape.common.DashArray;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2DConnection;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorViewTest {

    @Mock
    private OrthogonalPolyLine line;

    @Mock
    private Shape lineShape;

    private Point2DArray point2DArray;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPath headPath;

    @Mock
    private MultiPath tailPath;

    @Mock
    private MultiPathDecorator tailDecorator;

    private WiresConnectorView tested;

    @Mock
    private WiresConnectorControlImpl wiresConnectorControl;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        Node lineNode = line;
        Node headPathNode = headPath;
        Node tailPathNode = tailPath;

        point2DArray = new Point2DArray(new Point2D(0, 10), new Point2D(10, 10), new Point2D(20, 20), new Point2D(30, 30), new Point2D(40, 40));

        when(line.getPoint2DArray()).thenReturn(point2DArray);
        when(line.asShape()).thenReturn(lineShape);
        when(line.asNode()).thenReturn(lineNode);
        when(lineShape.setFillColor(anyString())).thenReturn(lineShape);
        when(lineShape.setFillColor(any(IColor.class))).thenReturn(lineShape);
        when(lineShape.setFillAlpha(anyDouble())).thenReturn(lineShape);
        when(lineShape.setStrokeColor(anyString())).thenReturn(lineShape);
        when(lineShape.setStrokeColor(any(IColor.class))).thenReturn(lineShape);
        when(lineShape.setStrokeAlpha(anyDouble())).thenReturn(lineShape);
        when(lineShape.setStrokeWidth(anyDouble())).thenReturn(lineShape);
        when(line.isControlPointShape()).thenReturn(true);
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        when(headPath.asNode()).thenReturn(headPathNode);
        when(tailPath.asNode()).thenReturn(tailPathNode);
        when(headPath.getBoundingBox()).thenReturn(new BoundingBox(0,
                                                                   0,
                                                                   10,
                                                                   10));
        this.tested = new WiresConnectorView(line,
                                             headDecorator,
                                             tailDecorator);
        this.tested.setControl(wiresConnectorControl);
        tested = spy(tested);
    }

    @Test
    public void testUUID() {
        final String uuid = "uuid";
        tested.setUUID(uuid);
        assertTrue(tested.getGroup().getUserData() instanceof WiresUtils.UserData);
        assertEquals(uuid,
                     tested.getUUID());
        assertEquals(uuid,
                     ((WiresUtils.UserData) tested.getGroup().getUserData()).getUuid());
    }

    @Test
    public void testCoordinates() {
        tested.setShapeLocation(new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(50.5, 321.65));
        assertEquals(50.5,
                     tested.getShapeX(),
                     0d);
        assertEquals(321.65,
                     tested.getShapeY(),
                     0d);
    }

    @Test
    public void testAlpha() {
        tested.setAlpha(0.53);
        assertEquals(0.53,
                     tested.getAlpha(),
                     0d);
    }

    @Test
    public void testFillAttributes() {
        tested.setFillColor("color1");
        tested.setFillAlpha(0.53);
        verify(lineShape,
               times(1)).setFillColor(eq("color1"));
        verify(line,
               times(1)).setFillAlpha(eq(0.53));
        verify(headPath,
               times(1)).setFillColor(eq("color1"));
        verify(headPath,
               times(1)).setFillAlpha(eq(0.53));
        verify(tailPath,
               times(1)).setFillColor(eq("color1"));
        verify(tailPath,
               times(1)).setFillAlpha(eq(0.53));
    }

    @Test
    public void testStrokeAttributes() {
        tested.setStrokeColor("color1");
        tested.setStrokeWidth(3.89);
        tested.setStrokeAlpha(0.53);
        verify(lineShape,
               times(1)).setStrokeColor(eq("color1"));
        verify(line,
               times(1)).setStrokeAlpha(eq(0.53));
        verify(lineShape,
               times(1)).setStrokeWidth(eq(3.89));
        verify(headPath,
               times(1)).setStrokeColor(eq("color1"));
        verify(headPath,
               times(1)).setStrokeAlpha(eq(0.53));
        verify(headPath,
               times(1)).setStrokeWidth(eq(3.89));
        verify(tailPath,
               times(1)).setStrokeColor(eq("color1"));
        verify(tailPath,
               times(1)).setStrokeAlpha(eq(0.53));
        verify(tailPath,
               times(1)).setStrokeWidth(eq(3.89));
    }

    @Test
    public void testConnectionControl() {
        final WiresConnectorControl connectorControl = mock(WiresConnectorControl.class);
        tested.setControl(connectorControl);
        assertEquals(connectorControl,
                     tested.getControl());
    }

    @Test
    public void testShowControlPoints() {
        final WiresConnectorControl connectorControl = mock(WiresConnectorControl.class);
        tested.setControl(connectorControl);
        final Object wcv = tested.showControlPoints(HasControlPoints.ControlPointType.POINTS);
        assertEquals(wcv,
                     tested);
        verify(connectorControl,
               times(1)).showControlPoints();
        verify(connectorControl,
               never()).hideControlPoints();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testShowControlPointsNotSupported() {
        final WiresConnectorControl connectorControl = mock(WiresConnectorControl.class);
        tested.setControl(connectorControl);
        tested.showControlPoints(HasControlPoints.ControlPointType.RESIZE);
    }

    @Test
    public void testHideControlPoints() {
        final WiresConnectorControl connectorControl = mock(WiresConnectorControl.class);
        tested.setControl(connectorControl);
        final Object wcv = tested.hideControlPoints();
        assertEquals(wcv,
                     tested);
        verify(connectorControl,
               times(1)).hideControlPoints();
        verify(connectorControl,
               never()).showControlPoints();
    }

    @Test
    public void testConnectMagnets() {
        MagnetManager.Magnets headMagnets = mock(MagnetManager.Magnets.class);
        WiresMagnet headMagnet = mock(WiresMagnet.class);
        when(headMagnets.getMagnet(eq(3))).thenReturn(headMagnet);
        when(headMagnet.getIndex()).thenReturn(3);
        Point2D headPoint = new Point2D(10,
                                        20);
        MagnetConnection headConnection = new MagnetConnection.Builder()
                .atX(10)
                .atY(20)
                .magnet(3)
                .build();
        MagnetManager.Magnets tailMagnets = mock(MagnetManager.Magnets.class);
        WiresMagnet tailMagnet = mock(WiresMagnet.class);
        when(tailMagnets.getMagnet(eq(7))).thenReturn(tailMagnet);
        when(tailMagnet.getIndex()).thenReturn(7);
        Point2D tailPoint = new Point2D(100,
                                        200);
        MagnetConnection tailConnection = new MagnetConnection.Builder()
                .atX(100)
                .atY(200)
                .magnet(7)
                .auto(true)
                .build();
        WiresConnection headWiresConnection = mock(WiresConnection.class);
        WiresConnection tailWiresConnection = mock(WiresConnection.class);
        tested.setHeadConnection(headWiresConnection);
        tested.setTailConnection(tailWiresConnection);
        tested.connect(headMagnets,
                       headPoint,
                       headConnection,
                       tailMagnets,
                       tailPoint,
                       tailConnection);
        verify(headWiresConnection,
               times(1)).setXOffset(eq(0d));
        verify(headWiresConnection,
               times(1)).setYOffset(eq(0d));
        verify(headWiresConnection,
               times(1)).setAutoConnection(eq(false));
        verify(headWiresConnection,
               times(1)).setMagnet(eq(headMagnet));
        verify(tailWiresConnection,
               times(1)).setXOffset(eq(0d));
        verify(tailWiresConnection,
               times(1)).setYOffset(eq(0d));
        verify(tailWiresConnection,
               times(1)).setAutoConnection(eq(true));
        verify(tailWiresConnection,
               times(1)).setMagnet(eq(tailMagnet));
    }

    @Test
    public void testConnectMagnetsByLocation() {

        // Mocks for head stuff.
        IControlHandleList headControlHandleList = new ControlHandleList(mock(Shape.class));
        WiresMagnet headMagnet1 = mock(WiresMagnet.class);
        when(headMagnet1.getX()).thenReturn(10d);
        when(headMagnet1.getY()).thenReturn(20d);
        when(headMagnet1.getIndex()).thenReturn(0);
        IPrimitive ch1 = mock(IPrimitive.class);
        when(ch1.getLocation()).thenReturn(new Point2D(10d,
                                                       20d));
        when(headMagnet1.getControl()).thenReturn(ch1);
        WiresMagnet headMagnet2 = mock(WiresMagnet.class);
        when(headMagnet2.getX()).thenReturn(100d);
        when(headMagnet2.getY()).thenReturn(200d);
        IPrimitive ch2 = mock(IPrimitive.class);
        when(ch2.getLocation()).thenReturn(new Point2D(100d,
                                                       200d));
        when(headMagnet2.getControl()).thenReturn(ch2);
        headControlHandleList.add(headMagnet1);
        headControlHandleList.add(headMagnet2);
        MagnetManager.Magnets headMagnets = mock(MagnetManager.Magnets.class);
        when(headMagnets.getMagnets()).thenReturn(headControlHandleList);
        Point2D headAbs = new Point2D(0,
                                      0);
        MagnetConnection headConnection = MagnetConnection.Builder.at(10,
                                                                      20);

        // Mocks for head tail.
        IControlHandleList tailControlHandleList = new ControlHandleList(mock(Shape.class));
        WiresMagnet tailMagnet1 = mock(WiresMagnet.class);
        when(tailMagnet1.getX()).thenReturn(10d);
        when(tailMagnet1.getY()).thenReturn(20d);
        IPrimitive th1 = mock(IPrimitive.class);
        when(th1.getLocation()).thenReturn(new Point2D(10d,
                                                       20d));
        when(tailMagnet1.getControl()).thenReturn(th1);
        WiresMagnet tailMagnet2 = mock(WiresMagnet.class);
        when(tailMagnet2.getX()).thenReturn(100d);
        when(tailMagnet2.getY()).thenReturn(200d);
        when(tailMagnet2.getIndex()).thenReturn(2);
        IPrimitive th2 = mock(IPrimitive.class);
        when(th2.getLocation()).thenReturn(new Point2D(100d,
                                                       200d));
        when(tailMagnet2.getControl()).thenReturn(th2);
        tailControlHandleList.add(tailMagnet1);
        tailControlHandleList.add(tailMagnet2);
        MagnetManager.Magnets tailMagnets = mock(MagnetManager.Magnets.class);
        when(tailMagnets.getMagnets()).thenReturn(tailControlHandleList);
        Point2D tailAbs = new Point2D(0,
                                      0);
        MagnetConnection tailConnection = MagnetConnection.Builder.at(100,
                                                                      200);

        // Mocks both source/target actual connections.
        WiresConnection headWiresConnection = mock(WiresConnection.class);
        WiresConnection tailWiresConnection = mock(WiresConnection.class);
        tested.setHeadConnection(headWiresConnection);
        tested.setTailConnection(tailWiresConnection);

        // Perform the connection.
        tested.connect(headMagnets,
                       headAbs,
                       headConnection,
                       tailMagnets,
                       tailAbs,
                       tailConnection);

        // Verify it sets the right magnets (closest to the location):
        verify(headWiresConnection,
               never()).setXOffset(anyDouble());
        verify(headWiresConnection,
               never()).setYOffset(anyDouble());
        verify(headWiresConnection,
               times(1)).setAutoConnection(eq(false));
        verify(headWiresConnection,
               times(1)).setMagnet(eq(headMagnet1));
        verify(tailWiresConnection,
               times(1)).setXOffset(eq(0d));
        verify(tailWiresConnection,
               times(1)).setYOffset(eq(0d));
        verify(tailWiresConnection,
               times(1)).setAutoConnection(eq(false));
        verify(tailWiresConnection,
               times(1)).setMagnet(eq(tailMagnet2));
    }

    @Test
    public void testConnectByPoints() {
        // Create the candidate connections based on some locations.
        final Point2DConnection headConnection =
                Point2DConnection.at(org.kie.workbench.common.stunner.core.graph.content.view.Point2D.create(13d, 56.6d));
        final Point2DConnection tailConnection =
                Point2DConnection.at(org.kie.workbench.common.stunner.core.graph.content.view.Point2D.create(88.4d, 1.2d));

        // Mocks both source/target actual connections.
        WiresConnection headWiresConnection = mock(WiresConnection.class);
        WiresConnection tailWiresConnection = mock(WiresConnection.class);
        tested.setHeadConnection(headWiresConnection);
        tested.setTailConnection(tailWiresConnection);

        // Perform the connection.
        tested.connect(null,
                       null,
                       headConnection,
                       null,
                       null,
                       tailConnection);

        // Verify it moves each connection to the right location, also settings null magnets.
        verify(headWiresConnection,
               times(1)).move(eq(13d), eq(56.6d));
        verify(headWiresConnection,
               times(1)).setAutoConnection(eq(false));
        verify(headWiresConnection,
               times(1)).setMagnet(eq(null));
        verify(tailWiresConnection,
               times(1)).move(eq(88.4d), eq(1.2d));
        verify(tailWiresConnection,
               times(1)).setAutoConnection(eq(false));
        verify(tailWiresConnection,
               times(1)).setMagnet(eq(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddControlPoint() {
        Point2D point1 = new Point2D(0.1, 0.2);
        ControlPoint controlPoint1 = ControlPoint.build(point1.getX(), point1.getY(), 0);
        IControlHandleList pointHandles = mock(IControlHandleList.class);
        when(pointHandles.isEmpty()).thenReturn(true);
        when(pointHandles.size()).thenReturn(0);
        tested.setPointHandles(pointHandles);
        List<ControlPoint> addedControlPoint = tested.addControlPoints(controlPoint1);
        assertFalse(addedControlPoint.isEmpty());
        assertEquals(controlPoint1, addedControlPoint.get(0));
        ArgumentCaptor<Point2DArray> c1 = ArgumentCaptor.forClass(Point2DArray.class);
        verify(line, atLeastOnce()).setPoint2DArray(c1.capture());
        Point2DArray points1 = c1.getValue();
        assertEquals(6, points1.size());
        assertEquals(point1, points1.get(1));
    }

    @Test
    public void testGetShapeControlPoints() {
        List<ControlPoint> controlPoints = tested.getShapeControlPoints();
        for (int i = 1; i < controlPoints.size() - 1; i++) {
            ControlPoint controlPoint = controlPoints.get(i);
            Point2D point = point2DArray.get(i);
            assertEquals(controlPoint.getLocation().getX(), point.getX(), 0);
            assertEquals(controlPoint.getLocation().getY(), point.getY(), 0);
            assertEquals(controlPoint.getIndex().intValue(), i);
        }
    }

    @Test
    public void testSetShadow() {
        tested.setShadow("red", 1, 2d, 3d);
        ArgumentCaptor<Shadow> shadowArgumentCaptor = ArgumentCaptor.forClass(Shadow.class);
        verify(line, times(1)).setShadow(shadowArgumentCaptor.capture());
        final Shadow shadow = shadowArgumentCaptor.getValue();
        assertEquals("red", shadow.getColor());
        assertEquals(1, shadow.getBlur());
        assertEquals(2d, shadow.getOffset().getX(), 0d);
        assertEquals(3d, shadow.getOffset().getY(), 0d);
    }

    @Test
    public void testRemoveShadow() {
        tested.removeShadow();
        verify(line, times(1)).setShadow((Shadow) isNull());
    }

    @Test
    public void testSetDashArray() {
        double dash = 1234;
        double[] dashes = {1, 2, 3, 4};
        DashArray dashArray = DashArray.create(dash, dashes);
        tested.setDashArray(dashArray);
        verify(line).setDashArray(dash, dashes);
    }
}