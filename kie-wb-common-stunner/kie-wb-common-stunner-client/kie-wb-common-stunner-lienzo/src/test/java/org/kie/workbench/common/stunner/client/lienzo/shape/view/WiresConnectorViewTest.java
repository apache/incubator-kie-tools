/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorViewTest {

    @Mock
    private OrthogonalPolyLine line;

    @Mock
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

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        Node lineNode = line;
        Node headPathNode = headPath;
        Node tailPathNode = tailPath;
        when(line.getPoint2DArray()).thenReturn(new Point2DArray(10,
                                                                 10));
        when(line.asNode()).thenReturn(lineNode);
        when(line.setFillColor(anyString())).thenReturn(line);
        when(line.setFillColor(any(IColor.class))).thenReturn(line);
        when(line.setFillAlpha(anyDouble())).thenReturn(line);
        when(line.setStrokeColor(anyString())).thenReturn(line);
        when(line.setStrokeColor(any(IColor.class))).thenReturn(line);
        when(line.setStrokeAlpha(anyDouble())).thenReturn(line);
        when(line.setStrokeWidth(anyDouble())).thenReturn(line);
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        when(headPath.asNode()).thenReturn(headPathNode);
        when(tailPath.asNode()).thenReturn(tailPathNode);
        this.tested = new WiresConnectorView(line,
                                             headDecorator,
                                             tailDecorator);
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
        tested.setShapeX(50.5);
        tested.setShapeY(321.65);
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
        verify(line,
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
        verify(line,
               times(1)).setStrokeColor(eq("color1"));
        verify(line,
               times(1)).setStrokeAlpha(eq(0.53));
        verify(line,
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
        final Object wcv = tested.setControl(connectorControl);
        assertEquals(wcv,
                     tested);
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
}
