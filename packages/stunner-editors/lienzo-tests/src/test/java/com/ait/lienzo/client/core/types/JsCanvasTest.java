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

package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.shape.wires.types.JsWiresShape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class JsCanvasTest {

    @Mock
    private JsCanvas jsCanvas;

    @Mock
    private JsWiresShape jsWiresShape;

    @Mock
    JSShapeStateApplier shapeStateApplier;

    @Test
    public void testGetBackgroundColor() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        when(jsWiresShape.getBackgroundColor()).thenReturn("blue");
        doCallRealMethod().when(jsCanvas).getBackgroundColor(any());

        final String backgroundColor = jsCanvas.getBackgroundColor("someID");
        assertEquals("blue", backgroundColor);
    }

    @Test
    public void testGetBackgroundColorNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).getBackgroundColor(any());

        String backgroundColor = jsCanvas.getBackgroundColor(null);
        assertEquals(null, backgroundColor);

        backgroundColor = jsCanvas.getBackgroundColor("");
        assertEquals(null, backgroundColor);
    }

    @Test
    public void testSetBackgroundColor() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).setBackgroundColor(any(), any());

        jsCanvas.setBackgroundColor("someID", "green");
        verify(jsWiresShape).setBackgroundColor("green");
    }

    @Test
    public void testSetBackgroundColorNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).setBackgroundColor(any(), any());

        jsCanvas.setBackgroundColor(null, "green");
        verify(jsWiresShape, never()).setBackgroundColor(any());

        jsCanvas.setBackgroundColor("", "green");
        verify(jsWiresShape, never()).setBackgroundColor(any());
    }

    @Test
    public void testSetBackgroundColorNullOrEmptyColor() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).setBackgroundColor(any(), any());

        jsCanvas.setBackgroundColor("someID", null);
        verify(jsWiresShape, never()).setBackgroundColor(any());
        jsCanvas.setBackgroundColor("someID", "");
        verify(jsWiresShape, never()).setBackgroundColor(any());
    }

    @Test
    public void testGetBorderColor() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        when(jsWiresShape.getBorderColor()).thenReturn("red");
        doCallRealMethod().when(jsCanvas).getBorderColor(any());

        final String borderColor = jsCanvas.getBorderColor("someID");
        assertEquals("red", borderColor);
    }

    @Test
    public void testGetBorderColorNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).getBorderColor(any());

        String borderColor = jsCanvas.getBorderColor(null);
        assertEquals(null, borderColor);
        borderColor = jsCanvas.getBorderColor("");
        assertEquals(null, borderColor);
    }

    @Test
    public void testSetBorderColor() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).setBorderColor(any(), any());

        jsCanvas.setBorderColor("someID", "black");
        verify(jsWiresShape).setBorderColor("black");
    }

    @Test
    public void testSetBorderColorNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).setBorderColor(any(), any());

        jsCanvas.setBorderColor(null, "black");
        verify(jsWiresShape, never()).setBorderColor(any());
        jsCanvas.setBorderColor("", "black");
        verify(jsWiresShape, never()).setBorderColor(any());
    }

    @Test
    public void testSetBorderColorNullOrEmptyColor() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).setBorderColor(any(), any());

        jsCanvas.setBorderColor("someId", null);
        verify(jsWiresShape, never()).setBorderColor(any());
        jsCanvas.setBorderColor("someId", "");
        verify(jsWiresShape, never()).setBorderColor(any());
    }

    @Test
    public void testGetLocation() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        Point2D location = new Point2D(100.0, 100.0);
        when(jsWiresShape.getLocationXY()).thenReturn(location);
        doCallRealMethod().when(jsCanvas).getLocation(any());

        NFastArrayList<Double> location2 = jsCanvas.getLocation("someID");
        assertEquals(location.getX(), location2.get(0), 0);
        assertEquals(location.getY(), location2.get(1), 0);
    }

    @Test
    public void testGetLocationNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).getLocation(any());

        NFastArrayList<Double> location = jsCanvas.getLocation(null);
        assertEquals(null, location);
        location = jsCanvas.getLocation("");
        assertEquals(null, location);
    }

    @Test
    public void testGetAbsoluteLocation() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        Point2D location = new Point2D(100.0, 100.0);
        when(jsWiresShape.getAbsoluteLocation()).thenReturn(location);
        doCallRealMethod().when(jsCanvas).getAbsoluteLocation(any());

        NFastArrayList<Double> location2 = jsCanvas.getAbsoluteLocation("someID");
        assertEquals(location.getX(), location2.get(0), 0);
        assertEquals(location.getY(), location2.get(1), 0);
    }

    @Test
    public void testGetAbsoluteLocationNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).getAbsoluteLocation(any());

        NFastArrayList<Double> location = jsCanvas.getAbsoluteLocation(null);
        assertEquals(null, location);
        location = jsCanvas.getAbsoluteLocation("");
        assertEquals(null, location);
    }

    @Test
    public void testGetDimensions() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        Point2D dimensions = new Point2D(100.0, 100.0);
        when(jsWiresShape.getBounds()).thenReturn(dimensions);
        doCallRealMethod().when(jsCanvas).getDimensions(any());

        NFastArrayList<Double> dimensions2 = jsCanvas.getDimensions("someID");
        assertEquals(dimensions.getX(), dimensions2.get(0), 0);
        assertEquals(dimensions.getY(), dimensions2.get(1), 0);
    }

    @Test
    public void testGetDimensionsNullOrEmptyUUID() {
        when(jsCanvas.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsCanvas).getDimensions(any());

        NFastArrayList<Double> dimensions = jsCanvas.getDimensions(null);
        assertEquals(null, dimensions);
        dimensions = jsCanvas.getDimensions("");
        assertEquals(null, dimensions);
    }

    @Test
    public void testApplyStateNulls() {
        jsCanvas.stateApplier = shapeStateApplier;
        doCallRealMethod().when(jsCanvas).applyState(anyString(), anyString());

        jsCanvas.applyState(null, null);
        verify(shapeStateApplier, never()).applyState(anyString(), anyString());

        jsCanvas.applyState("someId", null);
        verify(shapeStateApplier, never()).applyState(anyString(), anyString());

        jsCanvas.applyState(null, "none");
        verify(shapeStateApplier, never()).applyState(anyString(), anyString());
    }

    @Test
    public void testApplyStateNone() {
        jsCanvas.stateApplier = shapeStateApplier;
        doCallRealMethod().when(jsCanvas).applyState(anyString(), anyString());

        jsCanvas.applyState("someId", "none");
        verify(shapeStateApplier, times(1)).applyState("someId", "none");
    }

    @Test
    public void testApplyStateSelected() {
        jsCanvas.stateApplier = shapeStateApplier;
        doCallRealMethod().when(jsCanvas).applyState(anyString(), anyString());

        jsCanvas.applyState("someId", "selected");
        verify(shapeStateApplier, times(1)).applyState("someId", "selected");
    }

    @Test
    public void testApplyStateHighlight() {
        jsCanvas.stateApplier = shapeStateApplier;
        doCallRealMethod().when(jsCanvas).applyState(anyString(), anyString());

        jsCanvas.applyState("someId", "highlight");
        verify(shapeStateApplier, times(1)).applyState("someId", "highlight");
    }

    @Test
    public void testApplyStateInvalid() {
        jsCanvas.stateApplier = shapeStateApplier;
        doCallRealMethod().when(jsCanvas).applyState(anyString(), anyString());

        jsCanvas.applyState("someId", "invalid");
        verify(shapeStateApplier, times(1)).applyState("someId", "invalid");
    }

    @Test
    public void testCenter() {
        doCallRealMethod().when(jsCanvas).center(anyString());
        jsCanvas.center("someId");
        verify(jsCanvas, times(1)).centerNode("someId");
    }

    @Test
    public void testCenterNull() {
        doCallRealMethod().when(jsCanvas).center(anyString());
        jsCanvas.center(null);
        verify(jsCanvas, never()).centerNode(anyString());
    }
}
