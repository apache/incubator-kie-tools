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

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresColorMapIndexTest {

    @Mock
    private ColorMapBackedPicker picker;

    private WiresColorMapIndex tested;
    private ColorMapBackedPicker.PickerOptions pickerOptions;

    @Before
    public void setup() {
        pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);
        when(picker.getPickerOptions()).thenReturn(pickerOptions);
        tested = new WiresColorMapIndex(picker);
    }

    @Test
    public void testExclude() {
        WiresContainer shape = mock(WiresContainer.class);
        assertTrue(pickerOptions.getShapesToSkip().isEmpty());
        tested.exclude(shape);
        assertFalse(pickerOptions.getShapesToSkip().isEmpty());
        assertTrue(pickerOptions.getShapesToSkip().contains(shape));
    }

    @Test
    public void testBuild() {
        WiresLayer layer = mock(WiresLayer.class);
        NFastArrayList<WiresShape> children = new NFastArrayList<>();
        when(layer.getChildShapes()).thenReturn(children);
        tested.build(layer);
        verify(picker, times(1)).build(eq(children));
        verify(picker, never()).findShapeAt(anyInt(), anyInt());
        verify(picker, never()).clear();
    }

    @Test
    public void testFindShapeAt() {
        PickerPart part = mock(PickerPart.class);
        when(picker.findShapeAt(eq(3), eq(5))).thenReturn(part);
        assertEquals(part, tested.findShapeAt(3, 5));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        pickerOptions.getShapesToSkip().add(mock(WiresContainer.class));
        tested.clear();
        assertTrue(pickerOptions.getShapesToSkip().isEmpty());
        verify(picker, times(1)).clear();
        verify(picker, never()).build(any(NFastArrayList.class));
        verify(picker, never()).findShapeAt(anyInt(), anyInt());
    }
}
