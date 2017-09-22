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

package org.kie.workbench.common.stunner.client.lienzo.canvas;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.mockito.Mock;

import static org.kie.workbench.common.stunner.core.client.canvas.Layer.URLDataType.JPG;
import static org.kie.workbench.common.stunner.core.client.canvas.Layer.URLDataType.PNG;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasExportTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private LienzoLayer lienzoLayer;

    @Mock
    private Layer layer;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private Context2D context2D;

    private LienzoCanvasExport tested;

    @Before
    public void setup() {
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(lienzoLayer);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(layer.getWidth()).thenReturn(100);
        when(layer.getHeight()).thenReturn(200);
        when(scratchPad.getContext()).thenReturn(context2D);
        this.tested = new LienzoCanvasExport();
    }

    @Test
    public void testToJpgImageData() {
        tested.toImageData(canvasHandler,
                           JPG);
        verify(context2D,
               times(1)).setFillColor(eq(LienzoCanvasExport.BG_COLOR));
        verify(context2D,
               times(1)).fillRect(eq(0d),
                                  eq(0d),
                                  eq(100d),
                                  eq(200d));
        verify(layer,
               times(1)).drawWithTransforms(eq(context2D),
                                            eq(1d),
                                            any(BoundingBox.class));
        verify(scratchPad,
               times(1)).toDataURL(eq(DataURLType.JPG),
                                   eq(1d));
        verify(scratchPad,
               times(1)).clear();
    }

    @Test
    public void testToPngImageData() {
        tested.toImageData(canvasHandler,
                           PNG);
        verify(context2D,
               times(1)).setFillColor(eq(LienzoCanvasExport.BG_COLOR));
        verify(context2D,
               times(1)).fillRect(eq(0d),
                                  eq(0d),
                                  eq(100d),
                                  eq(200d));
        verify(layer,
               times(1)).drawWithTransforms(eq(context2D),
                                            eq(1d),
                                            any(BoundingBox.class));
        verify(scratchPad,
               times(1)).toDataURL(eq(DataURLType.PNG),
                                   eq(1d));
        verify(scratchPad,
               times(1)).clear();
    }
}
