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


package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoLayerUtilsTest {

    @Mock
    private LienzoLayer lienzoLayer;

    @Mock
    private Layer layer;

    @Before
    public void setup() {
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        when(layer.getLayer()).thenReturn(layer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getUUID_AtWhenOverShape() {
        final double x = 0.0;
        final double y = 0.0;
        final String expectedUUID = "uuid";
        whenThereIsAShapeAt(registerShape(expectedUUID).getPath(),
                            x,
                            y);

        final String actualUUID = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                              x,
                                                              y);
        assertEquals(expectedUUID,
                     actualUUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getUUID_AtWhenNotOverShape() {
        final double x = 0.0;
        final double y = 0.0;
        final String expectedUUID = "uuid";
        registerShape(expectedUUID);

        final String actualUUID = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                              x,
                                                              y);
        assertNull(actualUUID);
    }

    @Test
    public void testLayerToDataURL() {
        ScratchPad scratchPad = mock(ScratchPad.class);
        Context2D context2D = mock(Context2D.class);
        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(scratchPad.getContext()).thenReturn(context2D);
        when(scratchPad.toDataURL(eq(DataURLType.JPG), eq(1d))).thenReturn("theResultData");
        String result = LienzoLayerUtils.layerToDataURL(lienzoLayer,
                                                        DataURLType.JPG,
                                                        1,
                                                        3,
                                                        111,
                                                        333,
                                                        "#color1");
        assertEquals("theResultData", result);
        verify(scratchPad, times(1)).setPixelSize(eq(111), eq(333));
        verify(scratchPad, times(1)).clear();
        verify(context2D, times(1)).setFillColor(eq("#color1"));
        verify(context2D, times(1)).fillRect(eq(0d), eq(0d), eq(111d), eq(333d));
        verify(layer, times(1)).drawWithTransforms(eq(context2D),
                                                   eq(1d),
                                                   eq(BoundingBox.fromDoubles(1d,
                                                                              3d,
                                                                              111d,
                                                                              333d)));
    }

    private WiresShape registerShape(final String expectedUUID) {
        final Shape path = new MultiPath();
        final WiresShape ws = new WiresShape((MultiPath) path);

        WiresUtils.assertShapeUUID(ws.getContainer(),
                                   expectedUUID);
        return ws;
    }

    @SuppressWarnings("unchecked")
    private void whenThereIsAShapeAt(final Shape shape,
                                     final double x,
                                     final double y) {
        when(layer.findShapeAtPoint(eq((int) x),
                                    eq((int) y))).thenReturn(shape);
    }
}
