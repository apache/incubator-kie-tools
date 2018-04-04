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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresDockingControlImplTest {

    private WiresDockingControlImpl wiresDockingControl;

    private WiresShape shape;

    private WiresShape parent;

    private Layer layer;

    private WiresManager manager;

    private ColorMapBackedPicker.PickerOptions pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);

    @Mock
    private WiresParentPickerControlImpl parentPicker;

    @Mock
    private IDockingAcceptor dockingAcceptor;

    private static final double SHAPE_SIZE = 10;
    private static final double PARENT_SIZE = 100;

    @Before
    public void setUp() {

        layer = new Layer();
        manager = WiresManager.get(layer);

        shape = new WiresShape(new MultiPath().rect(0, 0, SHAPE_SIZE, SHAPE_SIZE));
        shape.setWiresManager(manager);
        parent = new WiresShape(new MultiPath().rect(0, 0, PARENT_SIZE, PARENT_SIZE));
        parent.setWiresManager(manager);
        manager.getMagnetManager().createMagnets(parent);
        shape.setLocation(new Point2D(0, 0));
        parent.setLocation(new Point2D(0, 0));

        when(parentPicker.getWiresLayer()).thenReturn(manager.getLayer());
        when(parentPicker.getParent()).thenReturn(parent);
        manager.setDockingAcceptor(dockingAcceptor);
        when(dockingAcceptor.dockingAllowed(parent, shape)).thenReturn(true);
        when(parentPicker.getParentShapePart()).thenReturn(PickerPart.ShapePart.BORDER);
        when(parentPicker.getShape()).thenReturn(shape);
        when(parentPicker.getCurrentLocation()).thenReturn(parent.getLocation());

        wiresDockingControl = new WiresDockingControlImpl(parentPicker);
    }

    @Test
    public void getAdjust() {
        wiresDockingControl.beforeMoveStart(0, 0);
        wiresDockingControl.afterMove(50, 50);
        Point2D adjust = wiresDockingControl.getAdjust();
        Point2D parentLocation = parent.getLocation();
        assertEquals(adjust.getX(), parentLocation.getX() - SHAPE_SIZE / 2, 0);
        assertEquals(adjust.getY(), parentLocation.getY() - SHAPE_SIZE / 2, 0);
    }

    @Test
    public void dock() {
        wiresDockingControl.dock(shape, parent, new Point2D(90, 90));
        Point2D location = shape.getLocation();
        assertEquals(location.getX(), 95, 0);
        assertEquals(location.getY(), 95, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(20, 85));
        location = shape.getLocation();
        assertEquals(location.getX(), 45, 0);
        assertEquals(location.getY(), 95, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(3, 92));
        location = shape.getLocation();
        assertEquals(location.getX(), -5, 0);
        assertEquals(location.getY(), 95, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(90, 10));
        location = shape.getLocation();
        assertEquals(location.getX(), 95, 0);
        assertEquals(location.getY(), -5, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(40, 15));
        location = shape.getLocation();
        assertEquals(location.getX(), 45, 0);
        assertEquals(location.getY(), -5, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(5, 10));
        location = shape.getLocation();
        assertEquals(location.getX(), -5, 0);
        assertEquals(location.getY(), -5, 0);
    }
}