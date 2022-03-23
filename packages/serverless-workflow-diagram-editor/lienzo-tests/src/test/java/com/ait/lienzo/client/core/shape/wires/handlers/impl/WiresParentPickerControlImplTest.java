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

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresParentPickerControlImplTest {

    private static final double START_X = 3;
    private static final double START_Y = 5;

    @Mock
    private ColorMapBackedPicker picker;

    @Mock
    private WiresLayerIndex wiresLayerIndex;

    @Mock
    private Viewport viewport;

    @Mock
    private ScratchPad scratchPad;

    private ColorMapBackedPicker.PickerOptions pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);
    private WiresParentPickerControlImpl tested;
    private Layer layer;
    private WiresManager manager;
    private WiresShape shape;
    private WiresShape parent;
    private WiresShapeLocationControlImpl shapeLocationControl;
    private Supplier<WiresLayerIndex> index;

    @Before
    public void setup() {
        layer = spy(new Layer());
        when(layer.getViewport()).thenReturn(viewport);

        manager = WiresManager.get(layer);
        shape = new WiresShape(new MultiPath().rect(0, 0, 10, 10));
        shape.setWiresManager(manager);
        parent = new WiresShape(new MultiPath().rect(0, 0, 100, 100));
        parent.setWiresManager(manager);
        shapeLocationControl = spy(new WiresShapeLocationControlImpl(shape));

        WiresColorMapIndex wiresColorMapIndex = new WiresColorMapIndex(picker);
        index = () -> wiresColorMapIndex;

        tested = new WiresParentPickerControlImpl(shapeLocationControl, index);
    }

    @Test
    public void testReturnParentAtCertainLocation() {
        // Start moving shape.
        tested.onMoveStart(START_X,
                           START_Y);
        assertEquals(manager.getLayer(), tested.getParent());
        // Mock find method to return parent at the following location.
        when(picker.findShapeAt(eq((int) (START_X + 10)),
                                eq((int) (START_Y + 10))))
                .thenReturn(new PickerPart(parent, PickerPart.ShapePart.BODY));

        // Move step. Parent is here.
        double dx = 10d;
        double dy = 10d;
        tested.onMove(dx, dy);
        assertEquals(parent, tested.getParent());

        // Move step. Parent no here.
        dx = -10d;
        dy = -10d;
        tested.onMove(dx, dy);
        assertEquals(manager.getLayer(), tested.getParent());
    }

    @Test
    public void testIndex() {
        ColorMapBackedPicker picker = spy(new ColorMapBackedPicker(scratchPad, pickerOptions));
        WiresColorMapIndex wiresColorMapIndex = new WiresColorMapIndex(picker);
        WiresParentPickerControlImpl tested = new WiresParentPickerControlImpl(shapeLocationControl,
                                                                               () -> wiresColorMapIndex);
        final WiresLayerIndex index = tested.getIndex();
        index.clear();
        assertTrue(pickerOptions.getShapesToSkip().isEmpty());
        index.exclude(mock(WiresShape.class));
        assertFalse(pickerOptions.getShapesToSkip().isEmpty());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(shapeLocationControl, atLeastOnce()).clear();
        verify(shapeLocationControl).destroy();
    }
}
