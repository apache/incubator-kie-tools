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

package com.ait.lienzo.client.core.shape.wires.picker;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ColorMapBackedPickerTest {

    public static final int X = 0;
    public static final int Y = 0;

    private ColorMapBackedPicker tested;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private ScratchPad scratchPad;

    private ColorMapBackedPicker.PickerOptions pickerOptions;

    private NFastArrayList<WiresShape> shapes;

    @Mock
    private WiresShape shape;

    @Mock
    private MultiPath path;

    private NFastArrayList<PathPartList> pathPartList;

    @Mock
    private PathPartList partList;

    @Mock
    private Context2D context;

    private Point2D location;

    @Mock
    private Viewport viewPort;

    @Mock
    private Layer layer;

    @Mock
    private Transform transform;

    @Mock
    private Transform inverseTransform;

    @Mock
    private ImageDataPixelColor pixelColor;

    @Mock
    private PickerPart pickerPart;

    @Before
    public void setUp() {
        pathPartList = new NFastArrayList<>(partList);
        shapes = new NFastArrayList<>(shape);
        pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);
        location = new Point2D(X, Y);

        when(shape.getPath()).thenReturn(path);
        when(path.getActualPathPartListArray()).thenReturn(pathPartList);
        when(scratchPad.getContext()).thenReturn(context);
        when(path.getComputedLocation()).thenReturn(location);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(layer.getViewport()).thenReturn(viewPort);
        when(viewPort.getTransform()).thenReturn(transform);
        when(transform.getInverse()).thenReturn(inverseTransform);
        when(context.getImageDataPixelColor(X, Y)).thenReturn(pixelColor);
        when(pixelColor.toBrowserRGB()).thenReturn(Color.rgbToBrowserHexColor(0, 0, 0));
        when(pickerPart.getShape()).thenReturn(shape);

        tested = new ColorMapBackedPicker(wiresLayer, shapes, scratchPad, pickerOptions);
    }

    @Test
    public void drawAndFindShapeAtTest() {
        tested.drawShape("#000000", 1, pickerPart, true);
        PickerPart shapeAt = tested.findShapeAt(X, Y);
        assertEquals(pickerPart, shapeAt);
        Point2D point = new Point2D(X, Y);
        verify(inverseTransform).transform(point, point);
        verify(context).getImageDataPixelColor(X, Y);
        verify(pixelColor).toBrowserRGB();
    }
}