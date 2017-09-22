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

package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
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
