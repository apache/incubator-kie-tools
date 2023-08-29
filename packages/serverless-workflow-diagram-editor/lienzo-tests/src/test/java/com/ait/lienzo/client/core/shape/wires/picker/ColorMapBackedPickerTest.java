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


package com.ait.lienzo.client.core.shape.wires.picker;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
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
    private ScratchPad scratchPad;

    @Mock
    private WiresShape shape;

    @Mock
    private MultiPath path;

    @Mock
    private PathPartList partList;

    @Mock
    private Context2D context;

    @Mock
    private ImageDataPixelColor pixelColor;

    @Mock
    private PickerPart pickerPart;

    @Before
    public void setUp() {
        NFastArrayList<PathPartList> pathPartList = new NFastArrayList<>();
        pathPartList.add(partList);
        ColorMapBackedPicker.PickerOptions pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);
        Point2D location = new Point2D(X, Y);

        when(shape.getPath()).thenReturn(path);
        when(shape.getGroup()).thenReturn(new Group());
        when(path.getActualPathPartListArray()).thenReturn(pathPartList);
        when(scratchPad.getContext()).thenReturn(context);
        when(path.getComputedLocation()).thenReturn(location);
        when(context.getImageDataPixelColor(X, Y)).thenReturn(pixelColor);
        when(pixelColor.toBrowserRGB()).thenReturn(Color.rgbToBrowserHexColor(0, 0, 0));
        when(pickerPart.getShape()).thenReturn(shape);
        tested = new ColorMapBackedPicker(scratchPad, pickerOptions);
    }

    @Test
    public void drawAndFindShapeAtTest() {

        tested.drawShape("#000000", 1, pickerPart, true);
        PickerPart shapeAt = tested.findShapeAt(X, Y);
        assertEquals(pickerPart, shapeAt);
        verify(context).getImageDataPixelColor(X, Y);
        verify(pixelColor).toBrowserRGB();
    }
}